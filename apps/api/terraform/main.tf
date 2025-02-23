terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "qwikcalai-terraform-state"
    key    = "state/terraform.tfstate"
    region = "us-east-1"
  }
}

provider "aws" {
  region = var.aws_region
}

# S3 buckets
resource "aws_s3_bucket" "uploads" {
  bucket = "${var.project_name}-uploads-${var.environment}"
}

resource "aws_s3_bucket_versioning" "uploads" {
  bucket = aws_s3_bucket.uploads.id
  versioning_configuration {
    status = "Enabled"
  }
}

# VPC for Aurora
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  version = "5.5.0"

  name = "${var.project_name}-vpc-${var.environment}"
  cidr = "10.0.0.0/16"

  azs             = ["${var.aws_region}a", "${var.aws_region}b"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24"]

  enable_nat_gateway = true
  single_nat_gateway = true
}

# Security group for Aurora
resource "aws_security_group" "aurora" {
  name        = "${var.project_name}-aurora-${var.environment}"
  description = "Security group for Aurora Serverless v2"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.api.id]
  }
}

# Aurora Serverless v2 cluster
resource "aws_rds_cluster" "main" {
  cluster_identifier     = "${var.project_name}-${var.environment}"
  engine                = "aurora-postgresql"
  engine_mode           = "provisioned"
  engine_version        = "15.4"
  database_name         = "qwikcalai"
  master_username       = "qwikcalai"
  master_password       = var.db_password
  skip_final_snapshot   = true
  vpc_security_group_ids = [aws_security_group.aurora.id]
  db_subnet_group_name  = aws_db_subnet_group.aurora.name

  serverlessv2_scaling_configuration {
    min_capacity = 0.5
    max_capacity = 1.0
  }
}

# Aurora Serverless v2 instance
resource "aws_rds_cluster_instance" "main" {
  identifier         = "${var.project_name}-${var.environment}-1"
  cluster_identifier = aws_rds_cluster.main.id
  instance_class     = "db.serverless"
  engine             = aws_rds_cluster.main.engine
  engine_version     = aws_rds_cluster.main.engine_version
}

# DB subnet group
resource "aws_db_subnet_group" "aurora" {
  name       = "${var.project_name}-aurora-${var.environment}"
  subnet_ids = module.vpc.private_subnets
}

# Security group for API
resource "aws_security_group" "api" {
  name        = "${var.project_name}-api-${var.environment}"
  description = "Security group for API"
  vpc_id      = module.vpc.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# SQS Queue for processing
resource "aws_sqs_queue" "event_processing" {
  name = "${var.project_name}-event-processing-${var.environment}"
  visibility_timeout_seconds = 300
  message_retention_seconds = 86400
}

# SNS Topic for notifications
resource "aws_sns_topic" "event_notifications" {
  name = "${var.project_name}-event-notifications-${var.environment}"
}

# Cognito User Pool
resource "aws_cognito_user_pool" "main" {
  name = "${var.project_name}-users-${var.environment}"
  
  password_policy {
    minimum_length = 8
    require_lowercase = true
    require_numbers = true
    require_symbols = true
    require_uppercase = true
  }

  username_attributes = ["email"]
  auto_verified_attributes = ["email"]
}

resource "aws_cognito_user_pool_client" "web_client" {
  name = "${var.project_name}-web-client"
  user_pool_id = aws_cognito_user_pool.main.id
  
  generate_secret = false
  explicit_auth_flows = [
    "ALLOW_USER_SRP_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH"
  ]
}
