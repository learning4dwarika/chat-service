terraform {
  backend "s3" {
    bucket  = "vca-aws-tf-state-global"
    region  = "ap-south-1"
    key     = "DEV/terraform.tfstate"
    encrypt = true
  }

  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "5.84.0"
    }
  }
  
}
