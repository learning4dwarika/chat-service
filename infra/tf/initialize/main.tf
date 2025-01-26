
# S3 bucket for storing the remote state of this/infra project
module "s3-terraform-state-global" {
  source               = "../modules/s3"
  bucket               = "dwchatapp-aws-tf-state"
  versioning_isenabled = false
}