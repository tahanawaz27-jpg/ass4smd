package com.example.smdass4.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Attachment implements Serializable {
    private String id;
    private String fileName;
    private String fileType; // "Image", "Document", "Text"
    private String fileSize;
    private String linkInfo;
    private String previewText; // Text content for document preview or image description

    public Attachment() {
        this.id = UUID.randomUUID().toString();
    }

    public Attachment(String id, String fileName, String fileType, String fileSize, String linkInfo, String previewText) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.linkInfo = linkInfo;
        this.previewText = previewText;
    }

    public Attachment copy() {
        return new Attachment(this.id, this.fileName, this.fileType, this.fileSize, this.linkInfo, this.previewText);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }

    public String getLinkInfo() { return linkInfo; }
    public void setLinkInfo(String linkInfo) { this.linkInfo = linkInfo; }

    public String getPreviewText() { return previewText; }
    public void setPreviewText(String previewText) { this.previewText = previewText; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileType, that.fileType) &&
                Objects.equals(fileSize, that.fileSize) &&
                Objects.equals(linkInfo, that.linkInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, fileType, fileSize, linkInfo);
    }
}