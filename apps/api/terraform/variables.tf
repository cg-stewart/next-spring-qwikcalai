variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Project name used in resource naming"
  type        = string
  default     = "qwikcalai"
}

variable "db_password" {
  description = "Password for RDS Aurora PostgreSQL database"
  type        = string
  sensitive   = true
}

variable "tags" {
  description = "Default tags for all resources"
  type        = map(string)
  default = {
    Project     = "QwikCalAI"
    Environment = "dev"
    ManagedBy   = "terraform"
  }
}

variable "github_owner" {
  description = "GitHub repository owner"
  type        = string
}

variable "github_repo" {
  description = "GitHub repository name"
  type        = string
}

variable "github_token" {
  description = "GitHub OAuth token for repository access"
  type        = string
  sensitive   = true
}

variable "openai_api_key" {
  description = "OpenAI API Key"
  type        = string
  sensitive   = true
}

variable "app_url" {
  description = "Application URL"
  type        = string
  default     = "http://localhost:3000"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}
