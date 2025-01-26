terraform {
  backend "s3" {
    bucket  = "dwchatapp-aws-tf-state"
    region  = "ap-south-1"
    key     = "DEV/terraform.tfstate"
    encrypt = true
    profile = "mfa"
  }

  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "5.84.0"
    }
  }
  
}
