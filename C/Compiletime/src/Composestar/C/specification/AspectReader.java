/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
 * 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: AspectReader.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.specification;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 18-jan-2005
 * Time: 11:49:50
 * To change this template use File | Settings | File Templates.
 */
public class AspectReader
{
    private PointcutFactory pointcutFactory = new PointcutFactory();
    private AdviceFactory adviceFactory = new AdviceFactory();

    public Aspect readAspectSpecification(String filename)
    {
        System.out.println("Reading aspect specification: " + filename);
        Aspect aspect = new Aspect();
        Document doc = parseXmlFile(filename, false);
        Element root = doc.getDocumentElement();

        aspect.setId(root.getAttribute("id"));

        NodeList pointcuts = doc.getElementsByTagName("pointcut");

        for(int i = 0; i < pointcuts.getLength(); i++)
        {
           Element element = (Element)pointcuts.item(i);
            Pointcut pointcut = pointcutFactory.createPointcut(element);
            pointcut.setParent(aspect);
            aspect.addPointcut(pointcut);
        }
        
        NodeList advices = doc.getElementsByTagName("advice");

        for(int i = 0; i < advices.getLength(); i++)
        {
           Element element = (Element)advices.item(i);
            Advice advice = adviceFactory.createAdvice(element);
            aspect.addAdvice(advice);
        }
        
        return aspect;
    }

    public static Document parseXmlFile(String filename, boolean validating)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);

            Document doc = factory.newDocumentBuilder().parse(new File(filename));
            return doc;
        }
        catch (SAXException e)
        {
            System.out.println(e.getMessage());
        }
        catch (ParserConfigurationException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
