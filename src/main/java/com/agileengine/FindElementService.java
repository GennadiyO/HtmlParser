package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FindElementService {
    public Optional<Elements> findElementById(File htmlFile, String key, String targetElement, String charSet) throws IOException {
        Document doc = Jsoup.parse(htmlFile, charSet, htmlFile.getAbsolutePath());
        return Optional.of(doc.getElementsByAttributeValue(key, targetElement));
    }
    public List<Optional<Elements>> findElementsForCompare(File htmlFile, Set<Element> elementList, String charSet) throws IOException{
        List<Optional<Elements>> optionalList = new ArrayList<>();
        Document doc = Jsoup.parse(htmlFile, charSet, htmlFile.getAbsolutePath());
        elementList.forEach(element ->
                element.attributes().forEach(attribute ->{
                    if (!attribute.getKey().equals("class")) {
                        optionalList.add(Optional.of(doc.getElementsByAttributeValue(attribute.getKey(), attribute.getValue())));
                    }
                }));
        return optionalList;
    }
}