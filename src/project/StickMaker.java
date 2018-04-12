package project;

//import com.itextpdf.barcodes.Barcode128;
//import com.itextpdf.barcodes.BarcodeQRCode;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import com.itextpdf.io.image.ImageDataFactory;
////import com.itextpdf.kernel.color.Color;
//import java.awt.*;
//import java.awt.color.*;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Image;
////import com.itextpdf.samples.GenericTest;
//import com.itextpdf.test.annotations.type.SampleTest;
//import sun.net.www.content.image.png;
////import org.junit.experimental.categories.Category;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.sun.org.apache.xerces.internal.xs.StringList;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import javax.swing.plaf.basic.BasicTextUI;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StickMaker {
    private static final BasicTextUI ImageDataFactory = null;
    stickerFromDb sticker = new stickerFromDb();
    Db data_base = null;
    String out_path = "E:\\PROJECTs\\Print_stick\\stikers\\raw\\out_file.pdf";

    public StickMaker() {
        data_base = new Db();
    }

    public String getStickBoxed(String qr_code) {
        String sticker_comment = "";
        if(qrCodeIsCorrect(qr_code)) {
            try {
                sticker_comment = getStickerFromDb(qr_code).comment.toString();
            } catch (BadElementException ex) {
                System.err.print(ex);
            }
        }
        return sticker_comment;
    }

    private stickerFromDb getStickerFromDb(String qr_code) throws BadElementException {
        // todo: db
        attributeStick stick_attribute = new attributeStick();
        stick_attribute.image_path = "E:\\PROJECTs\\Print_stick\\stikers\\raw\\sticker_progress.png";
        stick_attribute.barcode_code = "46200116630990";
        stick_attribute.serial_number = "10000001";
        stick_attribute.qr_code = qr_code;
        stick_attribute.serial_number_font_size = 45;
        stick_attribute.serial_number_x = 190;
        stick_attribute.serial_number_y = 440;
        //
        stick_attribute.use_barcode = true;
        stick_attribute.barcode_rote = true;
        stick_attribute.barcode_height = 80;
        stick_attribute.barcode_widht = 250;
        stick_attribute.point_barcode_x = 260;
        stick_attribute.point_barcode_y = 250;
        //
        stick_attribute.use_qr_code = true;
        stick_attribute.qr_height = 400;
        stick_attribute.qr_widht = 500;
        stick_attribute.point_qr_x = 10;
        stick_attribute.point_qr_y = 25;
        sticker.sticks.add(stick_attribute);
        sticker.comment = "";

        try {
            for(int countSticker=0; countSticker<sticker.sticks.size(); countSticker++) {
                stick_attribute = sticker.sticks.get(countSticker);
                com.itextpdf.text.Image img_sticker = com.itextpdf.text.Image.getInstance(stick_attribute.image_path);
                Document document = new Document(img_sticker);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(out_path));
                sticker.comment += "Высотка этикетки: " + img_sticker.getHeight() + "\r\n";
                sticker.comment += "Ширина этикетки: " + img_sticker.getWidth() + "\r\n";
                document.open();
                img_sticker.setAbsolutePosition(0, 0);
                document.add(img_sticker);
                // barcode
                if(stick_attribute.use_barcode) {
                    sticker.comment += "Barcode: используется" + "\r\n";
                    Barcode128 code128 = new Barcode128();
                    code128.setSize(8);
                    code128.setCode(stick_attribute.barcode_code);
                    sticker.comment += "Barcode номер: " + stick_attribute.barcode_code + "\r\n";
                    code128.setCodeType(Barcode128.CODE128);
                    PdfContentByte cb = writer.getDirectContent();
                    com.itextpdf.text.Image code128_image = code128.createImageWithBarcode(cb, BaseColor.BLACK, BaseColor.BLACK);
                    code128_image.scaleAbsolute(stick_attribute.barcode_widht, stick_attribute.barcode_widht);
                    code128_image.setAbsolutePosition(stick_attribute.point_barcode_x, stick_attribute.point_barcode_y);
                    if(stick_attribute.barcode_rote) {
                        code128_image.setRotationDegrees(90);
                    }
                    document.add(code128_image);
                    // serial number text
                    cb = writer.getDirectContent();
                    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
                    cb.setFontAndSize(bf, stick_attribute.serial_number_font_size);
                    cb.beginText();
                    cb.showTextAligned(PdfContentByte.ALIGN_CENTER, stick_attribute.serial_number, stick_attribute.serial_number_x, stick_attribute.serial_number_y, 0);
                    cb.endText();
                } else {
                    sticker.comment += "Barcode: не используется" + "\r\n";
                }
                // qr code
                if (stick_attribute.use_qr_code) {
                    BarcodeQRCode barcodeQRCode = new BarcodeQRCode(stick_attribute.qr_code, stick_attribute.qr_widht, stick_attribute.qr_height, null);
                    com.itextpdf.text.Image codeQrImage = barcodeQRCode.getImage();
                    codeQrImage.setAbsolutePosition(stick_attribute.point_qr_x, stick_attribute.point_qr_y);
                    document.add(codeQrImage);
                    sticker.comment += "Qr: используется" + "\r\n";
                    sticker.comment += "Qr номер: " + qr_code + "\r\n";
                }
                sticker.comment += "Документ создан успешно: " + "\r\n";
                sticker.comment += new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\r\n\r\n";
                document.close();
            }
        } catch (FileNotFoundException ex) {
            System.err.print(ex);
        } catch (DocumentException ex) {
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }
        return sticker;
    }

    public ArrayList<String> availablePrintSticker() {
        ArrayList <String> str = new ArrayList<String>();
        if(sticker.sticks.size() != 0) {
            str.add(sticker.comment);
        }
        return str;
    }

    private boolean qrCodeIsCorrect(String qr_code) {
        boolean res = false;
        //
        if(qr_code.length() != 0) {
            res = true;
        }
        //
        return res;
    }

    private class stickerFromDb {
        public String comment = "";
        public int count_stick = 0;
        ArrayList<attributeStick>sticks = new ArrayList<>();
    }
    private class attributeStick {
        boolean use_barcode = false;
        public String image_path;
        //
        public String serial_number;
        public int serial_number_font_size;
        public int serial_number_x;
        public int serial_number_y;
        //
        public String barcode_code;
        //
        public  String qr_code;
        public boolean use_qr_code;
        public int point_qr_x;
        public int point_qr_y;
        public int qr_height;
        public int qr_widht;
        //
        public boolean barcode_rote;
        public int point_barcode_x;
        public int point_barcode_y;
        public int barcode_height;
        public int barcode_widht;

        public attributeStick() {
            barcode_rote = false;
            barcode_code = "";
            serial_number = "";
            serial_number_font_size = 0;
            use_barcode = false;
            serial_number_x = 0;
            serial_number_y = 0;
            qr_code = "";
            String path = "";
            boolean use_qr_code = false;
            int point_barcode_x = 0;
            int point_barcode_y = 0;
            int point_qr_x = 0;
            int point_qr_y = 0;
            int barcode_height = 0;
            int barcode_widht = 0;
            int qr_height = 0;
            int qr_widht = 0;
        }
    }
}
