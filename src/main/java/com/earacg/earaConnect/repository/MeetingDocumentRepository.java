package com.earacg.earaConnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.earacg.earaConnect.model.MeetingDocument;

public interface MeetingDocumentRepository extends JpaRepository<MeetingDocument, Long>{
    Optional<MeetingDocument> findByFilePath(String filePath);
}
