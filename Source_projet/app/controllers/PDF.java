package controllers;

import controllers.BDDpackage.BDD;
import controllers.BDDpackage.Categorie;
import controllers.BDDpackage.SousCategorie;
import controllers.BDDpackage.Utilisateur;
import controllers.BDDpackage.Pays;
import controllers.BDDpackage.Statut;
import controllers.BDDpackage.Transaction;
import java.util.ArrayList;
/* Dependance pour PDF */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.text.Section;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.awt.Color;
import java.util.Date;
/**
 * Cette classe permet de créer un PDF des historiques d'un user
 */
public class PDF {

    Utilisateur user ;

    private static Font Title = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font Title2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private static Font info = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);


    PDF(Utilisateur user){
        this.user = user;
    }

    public boolean cree(){

        boolean statut = false;
        Document document = new Document();

        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Historique.pdf"));
            document.open();

            addMetaData(document);
            addTitlePage(document);
            createTable(document);

            document.close();
            writer.close();

            statut = true;
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        return statut;
    }

    private static void addMetaData(Document document) {
        document.addTitle("Historique de vos dépenses");
        document.addSubject("Historique");
        document.addKeywords("Historique, PDF, Compact Budget");
        document.addAuthor("Compact Budget");
        document.addCreator("Compact Budget");
    }

    private void addTitlePage(Document document)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Historique des mouvements financiers", Title));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(
                "Rapport généré pour : " + user.nom + " " + user.prenom + " le " + new Date() , //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                info));
        addEmptyLine(preface, 2);

        preface.add(new Paragraph(
                "Voici la liste de vos derniers mouvements financiers : ", Title2));

        addEmptyLine(preface, 4);

        document.add(preface);
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


    private void createTable(Document document) {
        try {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(90);

            // Style d'écriture utilisé
            Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
            Font green = new Font (new Phrase("Sous catégorie").getFont());
            green.setColor(BaseColor.GREEN);
            Font red = new Phrase("Sous catégorie").getFont();
            red.setColor(BaseColor.RED);

            PdfPCell cellHeader = new PdfPCell(new Phrase("Sous catégorie",bold));
            // Init cell header
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellHeader.setBorderWidth(2f);
            cellHeader.setFixedHeight(50f);
            cellHeader.setBackgroundColor(BaseColor.GRAY);

            table.addCell(cellHeader);

            cellHeader.setPhrase(new Phrase("Date",bold));
            table.addCell(cellHeader);

            cellHeader.setPhrase(new Phrase("Montant",bold));
            table.addCell(cellHeader);

            cellHeader.setPhrase(new Phrase("Solde",bold));
            table.addCell(cellHeader);

            // init cell content
            PdfPCell cellContente = new PdfPCell();
            cellContente.setMinimumHeight(25f);
            cellContente.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            cellContente.setVerticalAlignment(Element.ALIGN_MIDDLE);

            for ( Transaction transaction : (HomeController.DB).getAllTransaction(user.id) ){
                cellContente.setPhrase(new Phrase(transaction.name));
                table.addCell(cellContente);
                cellContente.setPhrase(new Phrase(transaction.date));
                table.addCell(cellContente);
                // Test le type de transaction (income - expense) pour la couleur du texte
                if (transaction.typeTransaction == 1)
                    cellContente.setPhrase(new Phrase(Double.toString(-(transaction.valeur)), red));
                else
                    cellContente.setPhrase(new Phrase('+' + Double.toString((transaction.valeur)), green));
                table.addCell( cellContente);

                cellContente.setPhrase(new Phrase(Double.toString((transaction.timestamp_solde))));
                table.addCell(cellContente);

            }

            document.add(table);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
