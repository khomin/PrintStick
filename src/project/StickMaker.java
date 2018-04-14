package project;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import com.sun.org.apache.xerces.internal.xs.StringList;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.swing.plaf.basic.BasicTextUI;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;

public class StickMaker {
    private static final BasicTextUI ImageDataFactory = null;
    private int sticker_parce_counter = 0;
    String last_qr_code = "";
    stickerFromDb sticker = new stickerFromDb();
    String out_path = "E:\\PROJECTs\\Print_stick\\stikers\\raw\\out_file.pdf";
    private Db data_base;

    public StickMaker() {
        data_base = Db.getInstance();
    }

    public String getStickBoxed(String qr_code, boolean print_mode) {
        String sticker_comment = "";
        if(qr_code == null) {
            qr_code = last_qr_code;
        } else {
            last_qr_code = qr_code;
        }
        if(qrCodeIsCorrect(qr_code)) {
            try {
                sticker_comment = getStickerFromDb(qr_code, print_mode).comment.toString();
            } catch (BadElementException ex) {
                System.err.print(ex);
            }
        }
        return sticker_comment;
    }

    private stickerFromDb getStickerFromDb(String qr_code,
                                           boolean print_auto_mode) throws BadElementException {
        //-- если крышка есть в серии
        if(data_base.checkDeviceQrCode(qr_code) == true) {
            //-- тогда загружаем jsonb с файлами и координатами для надписей
            sticker.sticks.clear();
            sticker.sticks.add(data_base.getStickers(qr_code));
            try {
                for(int countSticker=0; countSticker<sticker.sticks.size(); countSticker++) {
                    PdfContentByte cb;
                    com.itextpdf.text.Image img_sticker = com.itextpdf.text.Image.getInstance(sticker.sticks.get(0).bytea);
                    Document document = new Document(img_sticker);
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(out_path));
                    sticker.comment += "Высотка этикетки: " + img_sticker.getHeight() + "\r\n";
                    sticker.comment += "Ширина этикетки: " + img_sticker.getWidth() + "\r\n";
                    document.open();

                    img_sticker.setAbsolutePosition(0, 0);
                    document.add(img_sticker);
                    // barcode
                    if(sticker.sticks.get(countSticker).print_barcode) {
                        sticker.comment += "Barcode: используется" + "\r\n";
                        Barcode128 code128 = new Barcode128();
                        code128.setSize(8);
                        code128.setCode(sticker.sticks.get(countSticker).barcode_code);
                        sticker.comment += "Barcode номер: " + sticker.sticks.get(countSticker).barcode_code + "\r\n";
                        code128.setCodeType(Barcode128.CODE128);
                        cb = writer.getDirectContent();
                        com.itextpdf.text.Image code128_image = code128.createImageWithBarcode(cb, BaseColor.BLACK, BaseColor.BLACK);
                        code128_image.scaleAbsolute(sticker.sticks.get(countSticker).barcode_widht,
                                sticker.sticks.get(countSticker).barcode_height);
                        code128_image.setAbsolutePosition(sticker.sticks.get(countSticker).point_barcode_x,
                                sticker.sticks.get(countSticker).point_barcode_y);
                        if(sticker.sticks.get(countSticker).barcode_rote) {
                            code128_image.setRotationDegrees(90);
                        }
                        document.add(code128_image);
                    } else {
                        sticker.comment += "Barcode: не используется" + "\r\n";
                    }
                    // serial number text
                    if (sticker.sticks.get(countSticker).print_serial_num) {
                        cb = writer.getDirectContent();
                        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
                        cb.setFontAndSize(bf, sticker.sticks.get(countSticker).serial_number_font_size);
                        cb.beginText();
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                                sticker.sticks.get(countSticker).serial_number,
                                sticker.sticks.get(countSticker).serial_number_x,
                                sticker.sticks.get(countSticker).serial_number_y,
                                0);
                        cb.endText();
                    }

                    Map<EncodeHintType, Object> hints = new HashMap();
                    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

                    if(sticker.sticks.get(countSticker).print_qr) {
                        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(
                                sticker.sticks.get(countSticker).qr_code,
                                sticker.sticks.get(countSticker).qr_widht,
                                sticker.sticks.get(countSticker).qr_height,
                                hints);
                        com.itextpdf.text.Image codeQrImage = barcodeQRCode.getImage();
                        codeQrImage.setAbsolutePosition(
                                sticker.sticks.get(countSticker).point_qr_x,
                                sticker.sticks.get(countSticker).point_qr_y);
                        document.add(codeQrImage);
                        sticker.comment += "QR: используется" + "\r\n";
                        sticker.comment += "QR номер: " + qr_code + "\r\n";
                    }
                    document.close();
                    sticker.comment += "Документ создан успешно: " + "\r\n";
                    sticker.comment += new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\r\n\r\n";

                    if(print_auto_mode == true) {
                        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
                        DocPrintJob printJob = printService.createPrintJob();

                        PDDocument pdDocument = PDDocument.load(new File(out_path));
                        PDFPageable pdfPageable = new PDFPageable(pdDocument);
                        SimpleDoc doc = new SimpleDoc(pdfPageable, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
                        printJob.print(doc, null);
                        pdDocument.close();
                    }
                }} catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (PrintException e) {
                e.printStackTrace();
            }
        } else {
            sticker.sticks.clear();
        }
        sticker_parce_counter++;
        return sticker;
    }

    public boolean availablePrintSticker() {
        boolean res = false;
        if(sticker_parce_counter != 0) {
            if(sticker.sticks.size() != 0) {
                res = true;
            }
        }
        return res;
    }

    public ArrayList<String> getPrintStickerInformation() {
        ArrayList <String> str = new ArrayList<String>();
        if(sticker_parce_counter == 0) {
            if(sticker.sticks.size() != 0) {
                str.add(sticker.comment);
            }
        } else {
            if(sticker.sticks.size() != 0) {
                str.add(sticker.comment);
            } else {
                str.add("Ошибка разбора номера!\nНомер не прописан в БД!");
            }
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
        ArrayList<StickerAttributes>sticks = new ArrayList<>();
    }
}
