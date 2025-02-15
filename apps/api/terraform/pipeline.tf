resource "aws_codepipeline" "backend_pipeline" {
  name     = "${var.project_name}-backend-pipeline"
  role_arn = aws_iam_role.codepipeline_role.arn

  artifact_store {
    location = aws_s3_bucket.artifacts.id
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      name             = "Source"
      category         = "Source"
      owner           = "ThirdParty"
      provider        = "GitHub"
      version         = "1"
      output_artifacts = ["source_output"]

      configuration = {
        Owner      = var.github_owner
        Repo       = var.github_repo
        Branch     = "main"
        OAuthToken = var.github_token
      }
    }
  }

  stage {
    name = "Build"

    action {
      name            = "Build"
      category        = "Build"
      owner          = "AWS"
      provider       = "CodeBuild"
      input_artifacts = ["source_output"]
      version        = "1"

      configuration = {
        ProjectName = aws_codebuild_project.backend_build.name
      }
    }
  }
}

resource "aws_s3_bucket" "artifacts" {
  bucket = "${var.project_name}-pipeline-artifacts-${var.environment}"
}

resource "aws_codebuild_project" "backend_build" {
  name         = "${var.project_name}-backend-build"
  description  = "Build and test the Spring Boot backend"
  service_role = aws_iam_role.codebuild_role.arn

  artifacts {
    type = "CODEPIPELINE"
  }

  environment {
    compute_type                = "BUILD_GENERAL1_SMALL"
    image                      = "aws/codebuild/amazonlinux2-x86_64-standard:4.0"
    type                       = "LINUX_CONTAINER"
    image_pull_credentials_type = "CODEBUILD"

    environment_variable {
      name  = "ENVIRONMENT"
      value = var.environment
    }
  }

  source {
    type = "CODEPIPELINE"
    buildspec = "buildspec.yml"
  }
}

# IAM roles and policies will be defined in iam.tf
