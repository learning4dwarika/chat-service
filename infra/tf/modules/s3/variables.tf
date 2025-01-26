variable "env" {
  type = string
  default = "dev"
}
variable "bucket" {}
variable "versioning_isenabled" { default = false }
variable "server_side_encryption_configuration" { default = {} }
variable "key" { default = "" }
variable "sse" {
  type = object({
    sse_algorithm = string
  })
  default = {
    sse_algorithm = "AES256"
  }
}

variable "tags" {
  type    = map(string)
  default = {}
}

variable "nested_objects" {
  default = {}
}

locals {
  default_map_keys = {
    "product_code"             = "tf_learn"
    "iqr_environment"          = "${var.env == "prod" ? "prod" : "dev"}"
    "iqr_productID"            = "252"
    "iqr_product_businessarea" = "Global_Dealer_IT_Solutions"
    "iqr_product_name"         = "Communication_Insights"
    "iqr_product_suite"        = "Unified_Communication"
    "environment"              = var.env
    "versioning_status"        = "${var.versioning_isenabled == true ? "Enabled" : "Disabled"}"

  }
  tagsCollection = merge(local.default_map_keys, var.tags)
}

variable "bucket_trigger" {
   default = ""
}

variable "bucket_trigger_list" {
  type = list(object({
    lambda_function_arn = string
    events              = list(string)
    filter_prefix       = string
  }))
  default = []
}

variable "topic_trigger_list" {
   type = list(object({
    topic_arn = string
    events              = list(string)
    filter_prefix       = string
  }))
  default = []
}