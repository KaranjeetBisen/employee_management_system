
package com.advancedb.advancedb.model;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Component // Indicates that this class is a Spring Component, so it will be managed by Spring's dependency injection container.
public class PdfGenerator {

    // Hardcoded directory path where the PDF file will be saved
    private static final String DIRECTORY_PATH = "/home/admin/Desktop/Redmine_karanjeetb/training/advance_java/advanceJavaDB/advancedb/src/main/resources/pdfFiles/";

    // PDF file name, appending current date and time to ensure uniqueness
    private static final String FILE_NAME = "employee_list_" + LocalDateTime.now() + ".pdf";

    // Method to generate the PDF
    public void generate(List<Employee> empList, HttpServletResponse response) throws Exception {

        // Create the directory if it doesn't already exist
        Path directoryPath = Paths.get(DIRECTORY_PATH);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath); // Creates the directory if it doesn't exist
        }

        // Construct the full path for the PDF file
        String filePath = DIRECTORY_PATH + FILE_NAME;

        // Create a Document object to define the PDF layout
        Document document = new Document(PageSize.A4); // Setting the page size to A4

        // Creating an OutputStream to write the file and response
        try (OutputStream fileOutputStream = new FileOutputStream(filePath)) {
            
            // Writing the PDF content to the file
            PdfWriter.getInstance(document, fileOutputStream);
            
            // Also write the PDF content to the HttpServletResponse for returning to the client
            PdfWriter.getInstance(document, response.getOutputStream());

            // Open the document to begin adding content
            document.open();

            // Set up the title font
            Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
            fontTitle.setSize(20); // Set the font size to 20
            Paragraph paragraph1 = new Paragraph("List of the Employee", fontTitle); // Create title paragraph
            paragraph1.setAlignment(Paragraph.ALIGN_CENTER); // Center align the title
            document.add(paragraph1); // Add the title to the document

            // Create a table with 4 columns for EmpID, First Name, Full Name, and Department
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100); // Set table width to 100%
            table.setWidths(new int[] { 3, 3, 3, 3 }); // Set relative column widths
            table.setSpacingBefore(5); // Add some spacing before the table

            // Set up the table header row with blue background and white text
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(CMYKColor.BLUE); // Set cell background color to blue
            cell.setPadding(5); // Add padding to the cell
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN); // Set font to Times Roman
            font.setColor(CMYKColor.WHITE); // Set font color to white

            // Add headers for the table columns
            cell.setPhrase(new Phrase("EmpID", font)); // Set the text for EmpID column
            table.addCell(cell);
            cell.setPhrase(new Phrase("First Name", font)); // Set the text for First Name column
            table.addCell(cell);
            cell.setPhrase(new Phrase("Full Name", font)); // Set the text for Full Name column
            table.addCell(cell);
            cell.setPhrase(new Phrase("Department", font)); // Set the text for Department column
            table.addCell(cell);

            // Add data rows from the employee list to the table
            for (Employee emp : empList) {
                table.addCell(emp.getEmpid()); // Add employee ID
                table.addCell(emp.getFname()); // Add first name
                table.addCell(emp.getFullname()); // Add full name
                table.addCell(emp.getDepartment().toString()); // Add department name
            }

            // Add the table to the document
            document.add(table);

            // Close the document to finalize the PDF
            document.close();
        }
    }
}
