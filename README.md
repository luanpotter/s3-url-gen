# s3-url-gen

This is a simple Java CLI app to generate a Presigned URL from AWS S3 to allow you to upload an object.

This allows you to use your AWS credentials to make an URL that allows someone else a one time access to upload a file, with an expiration date.

This is tightly based on [this tutorial](https://docs.aws.amazon.com/AmazonS3/latest/dev/PresignedUrlUploadObjectJavaSDK.html).

## AWS Setup

First, create a bucket in AWS S3 and take note of the unique bucket name and the region.

For this bucket, eanble CORS if you'd like to use this URL in a web frontend application; adding the following CORS config will allow you to PUT (create) new objects from every domain. You can tweek that to your needs, of course.

```xml
<CORSConfiguration>
 <CORSRule>
   <AllowedOrigin>*</AllowedOrigin>
   <AllowedHeader>*</AllowedHeader>
   <AllowedMethod>PUT</AllowedMethod>
 </CORSRule>
</CORSConfiguration>
```

Finally, you'll need credentials to generate this URL. You can create a user with just write access to this particular bucket, for more security, or use a full access account, since it should remain hidden in your backend.

You will be able to provide the access token and key via parameters or use the default method with the config file in your home folder (like the one use by aws cli tooling).

## Project Setup

Clone this to your machine and use maven to build the project, or the script `./cmds/build.sh`.

After that, use the `./cmds/run.sh` script to run, providing the required parameters. For example:

```bash
./cmds/run.sh \
    -r <region> \ # region name, for example: us-east-2
    -b <bucket> \ # unique bucket name, for example: my-bucket
    -o <object> \ # object key to be created, or the file name. the user of the url won't be able to change ut
    -e <expiration> \ # the expiration time, from now, in seconds (it's double) (optional)
    --accessKey <accessKey> \ # the access key to your account (optional)
    --secretKey <secretKey> # the secret key to your account (optional)
```

If no credentials are provided, be sure to have some method of authentication as per described [here](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html).