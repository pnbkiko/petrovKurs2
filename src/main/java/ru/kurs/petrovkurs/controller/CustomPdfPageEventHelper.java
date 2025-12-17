package ru.kurs.petrovkurs.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomPdfPageEventHelper extends PdfPageEventHelper {
    private Font footerFont;

    public CustomPdfPageEventHelper() throws Exception {
        // Используем стандартный шрифт, если файл не найден
        try {
            BaseFont baseFont = BaseFont.createFont("assets/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            footerFont = new Font(baseFont, 8, Font.ITALIC, BaseColor.GRAY);
        } catch (Exception e) {
            // Если не удалось загрузить кастомный шрифт, используем стандартный
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            footerFont = new Font(baseFont, 8, Font.ITALIC, BaseColor.GRAY);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();

        // Нижний колонтитул
        Phrase footer = new Phrase(
                "Страница " + writer.getPageNumber() +
                        " | Отчет о просроченных ТО | " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                footerFont
        );

        ColumnText.showTextAligned(
                cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10,
                0
        );
    }
}