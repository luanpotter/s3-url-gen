package xyz.luan.aws.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import java.net.URL;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws JSAPException {
        JSAP jsap = configureJSAP();
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println();
            System.err.println("Usage: ");
            System.err.println(jsap.getUsage());
            System.err.println();
            System.exit(1);
        }

        String clientRegion = config.getString("region");
        String bucketName = config.getString("bucket");
        String objectKey = config.getString("object");
        double expirationSeconds = config.getDouble("expiration");

        boolean useSystemProps = setupAwsCredentials(config);
        URL url = generateUrl(useSystemProps, clientRegion, bucketName, objectKey, expirationSeconds);

        System.out.println("Generated url:");
        System.out.println(url.toString());
        System.out.println("Good bye!");
    }

    private static boolean setupAwsCredentials(JSAPResult config) {
        String accessKey = config.getString("accessKey");
        String secretKey = config.getString("secretKey");

        if (accessKey != null && secretKey != null) {
            System.setProperty("aws.accessKeyId", accessKey);
            System.setProperty("aws.secretKey", secretKey);
            return true;
        }

        return false;
    }

    private static URL generateUrl(boolean useSystemProps, String clientRegion, String bucketName, String objectKey, double expirationSeconds) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(useSystemProps ? new SystemPropertiesCredentialsProvider() : new ProfileCredentialsProvider())
                .withRegion(clientRegion)
                .build();

        Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * expirationSeconds;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    private static JSAP configureJSAP() throws JSAPException {
        JSAP jsap = new JSAP();

        jsap.registerParameter(new FlaggedOption("region")
                .setRequired(true)
                .setShortFlag('r')
                .setLongFlag("region"));

        jsap.registerParameter(new FlaggedOption("bucket")
                .setRequired(true)
                .setShortFlag('b')
                .setLongFlag("bucket"));

        jsap.registerParameter(new FlaggedOption("object")
                .setRequired(true)
                .setShortFlag('o')
                .setLongFlag("object"));

        jsap.registerParameter(new FlaggedOption("expiration")
                .setRequired(true)
                .setStringParser(JSAP.DOUBLE_PARSER)
                .setDefault("60")
                .setShortFlag('e')
                .setLongFlag("expiration"));

        jsap.registerParameter(new FlaggedOption("accessKey")
                .setRequired(false)
                .setLongFlag("accessKey"));

        jsap.registerParameter(new FlaggedOption("secretKey")
                .setRequired(false)
                .setLongFlag("secretKey"));

        return jsap;
    }
}
