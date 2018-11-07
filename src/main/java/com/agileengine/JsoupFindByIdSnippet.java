package com.agileengine;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsoupFindByIdSnippet {

    private static Logger LOGGER = LoggerFactory.getLogger(JsoupFindByIdSnippet.class);

    public static void main(String[] args) {

        Scanner scanner =  new Scanner(System.in);

        LOGGER.info("Please, provide the following data");
        LOGGER.info("Path to the source html file:");
        String sourceFilePath = scanner.nextLine();
        LOGGER.info("Path to the target html file:");
        String targetFilePath = scanner.nextLine();
        LOGGER.info("Expression for search:");
        String targetElementFilePath = scanner.nextLine();
        LOGGER.info("Type of expression:");
        String targetElementType = scanner.nextLine();

        if (sourceFilePath.isEmpty() || targetFilePath.isEmpty() || targetElementFilePath.isEmpty() || targetElementType.isEmpty()){
            throw new IllegalArgumentException("You did not provide required data!");
        }

        // Jsoup requires an absolute file path to resolve possible relative paths in HTML,
        // so providing InputStream through classpath resources is not a case
        String CHARSET_NAME = "utf8";

        FindElementService service = new FindElementService();
        Set<Element> elementSourceList = new HashSet<>();
        Set<Element> elementTargetList = new HashSet<>();
        try {
            Optional<Elements> buttonOts = service.findElementById(new File(sourceFilePath), targetElementType, targetElementFilePath, CHARSET_NAME);
            buttonOts.get().forEach(element -> elementSourceList.add(element));
            List<Optional<Elements>> optionalList = service.findElementsForCompare(new File(targetFilePath), elementSourceList, CHARSET_NAME);
            optionalList.forEach(optional -> optional.get().forEach(element -> elementTargetList.add(element)));
        } catch (IOException e){
            e.printStackTrace();
        }
        elementTargetList.forEach(elementTarget -> elementSourceList.forEach(elementSource ->{
            for (Attribute attribute : elementSource.attributes()) {
                if (!elementTarget.attributes().get(attribute.getKey()).isEmpty()){
                    if (!elementTarget.attributes().get(attribute.getKey()).equals(attribute.getValue())){
                        String newValue = elementTarget.attributes().get(attribute.getKey())+", and should be " + attribute.getValue();
                        elementTarget.attributes().remove(attribute.getKey());
                        elementTarget.attributes().put(attribute.getKey(), newValue);
                    } else {
                        elementTarget.attributes().remove(attribute.getKey());
                    }
                } else {
                    String newValue = "is empty, and should be " + attribute.getValue();
                    elementTarget.attributes().put(new Attribute(attribute.getKey(), newValue));
                }
            }
        }));
        elementTargetList.forEach(element ->
            LOGGER.info(element.attributes().asList().stream().map(attr ->
                    attr.getKey() + " = " + attr.getValue()).collect(Collectors.joining(", \n"))));
    }
    //TODO I didn't understand how output should be look like, so I did as I see :)
}