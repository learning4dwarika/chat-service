# Terraform : Infrastructure

This repository deploys chat-app to cloud. It uses AWS S3 to cache the state file to make sure state is constant irrespective of machine where the code is executed. The file is versioned to support rollback and also encrypted.

## Details

This repository sets up:

## Setup

1. Install the following locally:
   - [Terraform](https://www.terraform.io/) >= 0.10.0
2. Set up AWS credentials by export using =
   export AWS_SECRET_ACCESS_KEY=[] and export AWS_ACCESS_KEY_ID=[]

## Usage - Build

## Usage - Deploy

### More Information

- The project contains initialize folder. This terraform script creates S3 location required to store the tfplan. The state is stored locally in
  terraform_cache folder. Don't run the terraform script on this folder unless ofcourse you are starting from scratch.

### Initialize AWS for terraform deployment

cd ./initialize

```
terraform init -backend-config "profile=mfa"
$ terraform plan -out tfplan -var "aws_profile=mfa" -var "aws_region=ap-south-1"
```
or 
```
terraform plan --out=./terraform-cache/deploy.tfplan
terraform apply -auto-approve --input=false ./terraform-cache/deploy.tfplan
```
