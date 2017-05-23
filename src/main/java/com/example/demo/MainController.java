package com.example.demo;


import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Natallia on 22.05.2017.
 */
@Controller
public class MainController {

    private String jsonData = "";
    private String fileName = "output.xml";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String home() {
        return "home";
    }
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    String index(Model model) {
        model.addAttribute("data", getJsonData());
        return "index";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public
    @ResponseBody
    String Save(@RequestBody String data) throws JSONException, IOException {
        JSONArray jsonArr = new JSONArray(data);
        JSONObject o = new  JSONObject();
        for (int i = 0; i < jsonArr.length(); i++)
        {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            o.put("tag"+i, jsonObj);
        }
        String xml = convertJsontoXml(o.toString());
        writeFile(fileName, xml);
        return "redirect:/home";
    }

    public void setJsonData(String data){
        this.jsonData = data;
    }
    public String getJsonData(){
        return jsonData;
    }
    public static void writeFile(String filepath, String output) throws FileNotFoundException, IOException
    {
        FileWriter ofstream = new FileWriter(filepath);
        try (BufferedWriter out = new BufferedWriter(ofstream)) {
            out.write(output);
        }
    }
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public /*ResponseEntity<?>*/String  uploadFile(
            @RequestParam("uploadfile") MultipartFile uploadfile) {

        try {
            fileName = uploadfile.getOriginalFilename();
            parse(convertMultipartFileToFile(uploadfile));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "";//new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return "redirect:/index";//new ResponseEntity<>(HttpStatus.OK);
    }
    public static String convertJsontoXml(String json) throws JSONException
    {
        org.json.JSONObject jsonFileObject = new org.json.JSONObject(json);
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<root>\n"
                + org.json.XML.toString(jsonFileObject)+"\n</root>";
        return xml;
    }
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public void parse(File file){
        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            if (doc.hasChildNodes() && doc.getChildNodes().item(0).hasChildNodes()) {
                setJsonData(createJsonData(doc.getChildNodes().item(0).getChildNodes()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String createJsonData(NodeList nodeList) throws JSONException {
        JSONArray jArray = new JSONArray();
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            JSONObject jsonObject = new JSONObject();
            if(tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.hasChildNodes()){
                for (int c = 0; c < tempNode.getChildNodes().getLength(); c++) {
                    Node temp = tempNode.getChildNodes().item(c);
                    if (temp.getNodeType() == Node.ELEMENT_NODE) {
                        jsonObject.put(temp.getNodeName(), temp.getTextContent());
                    }
                }
            }
            if(jsonObject.length() != 0)
                jArray.put(jsonObject);
        }
        return jArray.toString();
    }
}

