resource "aws_ssm_parameter" "openai_api_key" {
  name        = "/${var.project_name}/${var.environment}/openai-api-key"
  description = "OpenAI API Key"
  type        = "SecureString"
  value       = var.openai_api_key
}

resource "aws_ssm_parameter" "cognito_client_secret" {
  name        = "/${var.project_name}/${var.environment}/cognito-client-secret"
  description = "Cognito Client Secret"
  type        = "SecureString"
  value       = aws_cognito_user_pool_client.web_client.client_secret
}

resource "aws_ssm_parameter" "app_url" {
  name        = "/${var.project_name}/${var.environment}/app-url"
  description = "Application URL"
  type        = "String"
  value       = var.app_url
}
