output "cognito_user_pool_id" {
  value = aws_cognito_user_pool.main.id
}

output "cognito_client_id" {
  value = aws_cognito_user_pool_client.web_client.id
}

output "s3_bucket_name" {
  value = aws_s3_bucket.uploads.id
}

output "sqs_queue_url" {
  value = aws_sqs_queue.event_processing.url
}

output "sns_topic_arn" {
  value = aws_sns_topic.event_notifications.arn
}
