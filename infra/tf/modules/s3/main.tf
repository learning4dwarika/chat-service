
resource "aws_s3_bucket" "this" {
    bucket = "${var.bucket}-${var.env}"
    tags = local.tagsCollection
}

resource "aws_s3_bucket_versioning" "this" {
  bucket = aws_s3_bucket.this.id
  versioning_configuration {
    status = "${local.default_map_keys.versioning_status}"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "this" {
  bucket = aws_s3_bucket.this.id

  rule {
    apply_server_side_encryption_by_default {
      # kms_master_key_id = aws_kms_key.mykey.arn
      sse_algorithm     = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "this" {
  bucket = aws_s3_bucket.this.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_ownership_controls" "this" {
  bucket = aws_s3_bucket.this.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_acl" "this" {
  depends_on = [aws_s3_bucket_ownership_controls.this]

  bucket = aws_s3_bucket.this.id
  acl    = "private"
}

resource "aws_s3_object" "folders" {
  count  = length(var.nested_objects)
  bucket = aws_s3_bucket.this.id
  key    = "${var.nested_objects[count.index]}/"
  server_side_encryption = "AES256"
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  count = length(var.bucket_trigger_list)
  bucket = var.bucket_trigger
  dynamic "lambda_function" {
    for_each = var.bucket_trigger_list
    content { 
        lambda_function_arn = lambda_function.value.lambda_function_arn
        events              = lambda_function.value.events
        filter_prefix       = lambda_function.value.filter_prefix
      } 
    }
  dynamic "topic" {
    for_each = var.topic_trigger_list
    content { 
        topic_arn = topic.value.topic_arn
        events              = topic.value.events
        filter_prefix       = topic.value.filter_prefix
      } 
    }
}