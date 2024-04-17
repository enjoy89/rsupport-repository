package rsupport.jeondui.notice.common.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.AmazonS3Exception;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * AWS S3에 첨부 파일 업로드
     */
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "." + file.getOriginalFilename();
        String filePath = generateFilePath(fileName);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            s3Client.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), metadata));
            return fileName;
        } catch (IOException e) {
            throw new AmazonS3Exception(ErrorCode.AWS_S3_FILE_UPLOAD_FAIL);
        }
    }

    /**
     * AWS S3에서 첨부 파일 삭제
     */
    public void deleteFile(String filePath) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
        } catch (AmazonServiceException e) {
            throw new AmazonS3Exception(ErrorCode.AWS_S3_FILE_DELETE_FAIL);
        }
    }

    /**
     * AWS S3에 업로드한 파일 객체 URL 반환
     */
    public String getFileUrl(String fileName) {
        return s3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * 파일 저장 경로 반환 메서드
     */
    private String generateFilePath(String fileName) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s/%s", currentDate, fileName);
    }

}
