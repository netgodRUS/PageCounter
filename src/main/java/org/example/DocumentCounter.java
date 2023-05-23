package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.poi.xwpf.usermodel.XWPFDocument;





class DocumentPageCounter {
    private int totalDocuments;
    private int totalPages;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the path to the root folder.");
            return;
        }

        String rootFolderPath = args[0];

        DocumentPageCounter counter = new DocumentPageCounter();
        counter.countPages(rootFolderPath);

        System.out.println("Total Documents: " + counter.totalDocuments);
        System.out.println("Total Pages: " + counter.totalPages);
    }

    public void countPages(String rootFolderPath) {
        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            System.out.println("Invalid root folder path.");
            return;
        }

        processFolder(rootFolder);
    }

    private void processFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processFolder(file);
                } else {
                    processFile(file);
                }
            }
        }
    }

    private void processFile(File file) {
        String fileName = file.getName();
        if (fileName.toLowerCase().endsWith(".docx")) {
            countPagesInWordDocument(file);
        } else if (fileName.toLowerCase().endsWith(".pdf")) {
            countPagesInPdfDocument(file);
        }
    }

    private void closeXWPFDocument(XWPFDocument document) {
        if (document != null) {
            document.createParagraph();
        }
    }



    private void countPagesInWordDocument(File file) {
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(new FileInputStream(file));
            int pageCount = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
            totalDocuments++;
            totalPages += pageCount;
        } catch (IOException e) {
            throw new RuntimeException("Error reading Word document: " + file.getAbsolutePath(), e);
        } finally {
            closeXWPFDocument(document);
        }
    }







    private void countPagesInPdfDocument(File file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            int pageCount = document.getNumberOfPages();
            totalDocuments++;
            totalPages += pageCount;
        } catch (IOException e) {
            System.out.println("Error reading PDF document: " + file.getAbsolutePath());
        }
    }
}


