package org.sopt.kareer.global.external.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.external.ai.dto.response.DocumentUploadResponse;
import org.sopt.kareer.global.external.ai.service.RagService;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.UPLOAD_PDF;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/documents")
public class RagController {

    private final RagService documentService;

    @Tag(name = "RAG 관련 API")
    @Operation(summary = "정책 PDF 문서 업로드 (Server Only)" , description = "정책 관련 PDF 문서를 임베딩하여 vectorDB에 저장합니다.")
    @CustomExceptionDescription(UPLOAD_PDF)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<DocumentUploadResponse>> uploadPdfFile(
            @Parameter(description = "업로드할 PDF 파일", required = true)
            @RequestParam("file") MultipartFile file){
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(documentService.uploadDocument(file), "PDF 문서 업로드 성공"));
    }

}
