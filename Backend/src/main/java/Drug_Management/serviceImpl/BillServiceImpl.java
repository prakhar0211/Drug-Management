package Drug_Management.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Drug_Management.JWT.JwtFilter;
import Drug_Management.POJO.Bill;
import Drug_Management.constants.DrugConstants;
import Drug_Management.dao.BillDao;
import Drug_Management.service.BillService;
import Drug_Management.utils.DrugUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

	@Autowired
	JwtFilter jwtFilter;

	@Autowired
	BillDao billDao;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		log.info("Inside generateReport");
		try {
			String fileName;
			if (validateRequestMap(requestMap)) {
				if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
					fileName = (String) requestMap.get("uuid");
				} else {
					fileName = DrugUtils.getUUID();
					requestMap.put("uuid", fileName);
					insertBill(requestMap);
				}
				String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: "
						+ requestMap.get("contactNumber") + "\n" + "Email: " + requestMap.get("email") + "\n"
						+ "Address: " + requestMap.get("address");
				Document document = new Document();
				PdfWriter.getInstance(document,
						new FileOutputStream(DrugConstants.Store_Location + "\\" + fileName + ".pdf"));
				document.open();
				setRectangleInPdf(document);

				Paragraph chunk = new Paragraph("Drug Supply Management System", getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);

				Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
				document.add(paragraph);

				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);

				JSONArray jsonArray = DrugUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
				for (int i = 0; i < jsonArray.length(); i++) {
					addRows(table, DrugUtils.getMapFromJson(jsonArray.getString(i)));
				}
				document.add(table);

				Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n"
						+ "Thank you for Purchasing. Please visit again!!", getFont("Data"));
				document.add(footer);
				document.close();
				return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);
			}
			return DrugUtils.getResponseEntity("Required Data not found", HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		// TODO Auto-generated method stub
		log.info("Inside add rows");
		table.addCell((String) data.get("name"));
		table.addCell((String) data.get("category"));
		table.addCell((String) data.get("quantity"));
		table.addCell(Double.toString((Double) data.get("price")));
		table.addCell(Double.toString((Double) data.get("total")));
	}

	private void addTableHeader(PdfPTable table) {
		// TODO Auto-generated method stub
		log.info("Inside addTableHeader");
		Stream.of("Name", "Category", "Quantity", "Price", "Sub Total").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.MAGENTA);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			header.setBorderColor(BaseColor.RED);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		});
	}

	private Font getFont(String type) {
		log.info("Inside get font");
		switch (type) {
		case "Header": {

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 20, BaseColor.DARK_GRAY);
			headerFont.setStyle(Font.BOLD);
			return headerFont;

		}
		case "Data": {
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13, BaseColor.DARK_GRAY);
			dataFont.setStyle(Font.BOLD);
			return dataFont;
		}
		default:
			return new Font();
		}
	}

	private void setRectangleInPdf(Document document) throws DocumentException {
		// TODO Auto-generated method stub
		log.info("Inside setrectangleinpdf");
		Rectangle rect = new Rectangle(577, 825, 18, 15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.BLACK);
		rect.setBorderWidth(1);
		document.add(rect);
	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String) requestMap.get("name"));
			bill.setEmail((String) requestMap.get("email"));
			bill.setContactNumber((String) requestMap.get("contactNumber"));
			bill.setAddress((String) requestMap.get("address"));
			bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
			bill.setProductDetail((String) requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean validateRequestMap(Map<String, Object> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("address")
				&& requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");

	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		List<Bill> list = new ArrayList<>();
		if (jwtFilter.isAdmin()) {
			list = billDao.getAllBills();

		} else {
			list = billDao.getBillByUserName(jwtFilter.getCurrentUser());
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		log.info("Inside getPdf:requestMap {}", requestMap);
		try {
			byte[] byteArray = new byte[0];
			if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
				return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
			String filePath = DrugConstants.Store_Location + "\\" + (String) requestMap.get("uuid") + ".pdf";
			if (DrugUtils.isFileExist(filePath)) {
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray, HttpStatus.OK);
			} else {
				requestMap.put("isGenerate", false);
				generateReport(requestMap);
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray, HttpStatus.OK);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private byte[] getByteArray(String filePath) throws Exception {
		File initialFile = new File(filePath);
		InputStream targerStream = new FileInputStream(initialFile);
		byte[] byteArray = IOUtils.toByteArray(targerStream);
		targerStream.close();
		return byteArray;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional optional = billDao.findById(id);
			if (!optional.isEmpty()) {
				billDao.deleteById(id);
				return DrugUtils.getResponseEntity("Bill deleted Successfully", HttpStatus.OK);
			}
			return DrugUtils.getResponseEntity("Bill id does not exist", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
