package opensrpadapter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.io.IOUtils;
import org.mpower.form.XMLData;
import org.mpower.form.SubmissionBuilder;

@Controller
@RestController
public class ServiceController {
	
    @RequestMapping("/sendDataXML")
    @Produces("application/json")
    public @ResponseBody String sendDataXML(@RequestParam(value = "message", defaultValue = "hello") String message) {
    	
    	System.out.println("Receive data successfully !!" + message);    	
    	return "{success : true}";
    }     

    @RequestMapping(value="/upload", headers = "content-type=multipart/*", method=RequestMethod.POST)
    @Consumes("multipart/form-data")
    @Produces("application/json")
    public @ResponseBody String dataXMLReceiver(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
    	ByteArrayInputStream fileIS = null;
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = 
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                fileIS = new ByteArrayInputStream(bytes);
                String myString = IOUtils.toString(fileIS, "UTF-8");
                stream.write(bytes);
                stream.close();
                System.out.println("Received a submission successfully file name: " + name);   
                System.out.println("Received a submission successfully file data: " + myString);   
            } catch (Exception e) {
            	System.out.println("Failed to upload " + name + " => " + e.getMessage());
            }
        } else {
        	System.out.println("Failed to upload : " + name + ", because the file was empty.");
        }    	

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String entityID = "";
		try {
			XMLData.setXmlDocument(builder.parse(fileIS));
			entityID = SubmissionBuilder.buildFormSubmission(name);			
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	return "{entityID :" + entityID + "}";
    } 

}
