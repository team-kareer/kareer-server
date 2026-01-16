package org.sopt.kareer.global.external.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.external.ai.dto.request.JobPostingEmbeddingRequest;
import org.sopt.kareer.global.external.ai.dto.response.DocumentUploadResponse;
import org.sopt.kareer.global.external.ai.service.RagService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.UPLOAD_JOBPOSTING;
import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.UPLOAD_PDF;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/rag")
public class RagController {

    private final RagService ragService;

    @Tag(name = "RAG 관련 API")
    @Operation(summary = "정책 PDF 문서 업로드 (Server Only)" , description = "정책 관련 PDF 문서를 임베딩하여 vectorDB에 저장합니다.")
    @CustomExceptionDescription(UPLOAD_PDF)
    @PostMapping(value = "policy",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<DocumentUploadResponse>> uploadPdfFile(
            @Parameter(description = "업로드할 PDF 파일", required = true)
            @RequestParam("file") MultipartFile file){
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(ragService.uploadPolicyDocument(file), "PDF 문서 업로드 성공"));
    }

    @Tag(name = "RAG 관련 API")
    @Operation(summary = "채용 공고 임베딩 (Server Only)", description = "채용 공고 정보를 임베딩하여 vectorDB에 저장합니다.")
    @CustomExceptionDescription(UPLOAD_JOBPOSTING)
    @PostMapping("job-posting")
    public ResponseEntity<BaseResponse<DocumentUploadResponse>> uploadJobPosting(
            @RequestBody JobPostingEmbeddingRequest request
            ){

        ragService.embedJobPosting(request.jobPostingId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("채용 공고 임베딩 성공"));
    }

}
