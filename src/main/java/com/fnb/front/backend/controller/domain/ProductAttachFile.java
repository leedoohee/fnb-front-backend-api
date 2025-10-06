package com.fnb.front.backend.controller.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name="product_attach_file")
public class ProductAttachFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "file_name", nullable = false, length = 512)
    private String fileName; // 서버에 실제로 저장된 파일명 (UUID 등)

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath; // 서버 내의 파일 저장 경로

    @Column(name = "product_id", nullable = false, length = 512)
    private int productId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ProductAttachFile() {

    }
}
