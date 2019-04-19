package com.wskj.project.util;

public class ImgUploadUtils {
    /**
     * 通过文件名判断并获取文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static String getContentType(String fileName) {
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        //视频格式
        if (".mp4".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".avi".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".wmv".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".mkv".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".flv".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".rmvb".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        if (".pdf".equalsIgnoreCase(fileExtension)) {
            return "application/pdf";
        }
        if (".xls".equalsIgnoreCase(fileExtension)) {
            return "application/x-xls";
        }
        //默认返回类型
        return "image/jpeg";
    }


}
