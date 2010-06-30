/*  Copyright 2008 Fabrizio Cannizzo
 *
 *  This file is part of RestFixture.
 *
 *  RestFixture (http://code.google.com/p/rest-fixture/) is free software:
 *  you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  RestFixture is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RestFixture.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  If you want to contact the author please leave a comment here
 *  http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html
 */
package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.junit.Test;

public class ToolsTest {
	@Test
	public void mustMatchWhenRegexIsValidAndThereIsAMatch() {
		assertTrue(Tools.regex("200", "200"));
	}
	@Test
	public void mustNotMatchWhenRegexIsValidAndThereIsNotAMatch() {
		assertFalse(Tools.regex("200", "404"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void mustNotMatchWhenRegexIsInvalidAndNotifyError() {
		Tools.regex("200", "40[]4");
		fail("Should have thrown IAE as expression is invalid");
	}

	@Test
	public void dualityOfToAndFromHtml(){
		String stuff = "<a> " + System.getProperty("line.separator") + "  </a>";
		assertEquals(stuff, Tools.fromHtml(Tools.toHtml(stuff)));
	}

	@Test
	public void shouldReadAnInputStreamToAString(){
		InputStream is = new ByteArrayInputStream("a string".getBytes());
		assertEquals("a string", Tools.getStringFromInputStream(is));
		assertEquals("", Tools.getStringFromInputStream(null));
	}

	@Test
	public void shouldWrapAStringIntoAnInputStream(){
		InputStream is = Tools.getInputStreamFromString("another string");
		assertEquals("another string", Tools.getStringFromInputStream(is));
	}

	@Test
	public void shouldConvertAMapIntoAStringRepresentation(){
		final Map<String, String> map = new HashMap<String, String>();
		map.put("k1", "v1");
		map.put("k2", "v2");
		final String nvSep = "|";
		final String entrySep = "##";
		String repr = Tools.convertMapToString(map, nvSep, entrySep);
		assertEquals("k1|v1##k2|v2", repr);
	}

	@Test
	public void shouldConvertAStringIntoAMap(){
		Map<String, String> map = Tools.convertStringToMap("k1|v1##k2|v2", "|", "##");
		assertEquals(2, map.size());
		assertEquals("v2", map.get("k2"));
		assertEquals("v1", map.get("k1"));

		map = Tools.convertStringToMap("k1##k2|v2", "|", "##");
		assertEquals(2, map.size());
		assertEquals("", map.get("k1"));
		assertEquals("v2", map.get("k2"));

	}

	@Test
	public void shouldExtractXPathsFromXmlDocumentAsNodeLists() {
		String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
		assertEquals(2, Tools.extractXPath("/a/c", xml).getLength());
		assertEquals(1, Tools.extractXPath("/a/b[text()='test']", xml)
				.getLength());
		assertEquals("test", Tools.extractXPath("/a/b/text()", xml).item(0)
				.getNodeValue());
		assertEquals(1, Tools.extractXPath("/a[count(c)>0]", xml)
				.getLength());
		assertEquals(3, Tools.extractXPath("/a/b | /a/c | /a/X", xml)
				.getLength());
		assertEquals(3, Tools.extractXPath("/a/b | /a/c | /a/X", xml)
				.getLength());
	}

	@Test
	public void shouldExtractXPathsFromXmlDocumentAsStrings(){
		String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
		assertEquals("2", Tools.extractXPath("count(/a/c)", xml,
				XPathConstants.STRING));

	}

	@Test
	public void shouldExtractXPathsFromXmlDocumentAsNumber() {
		String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
		assertEquals(1.0, Tools.extractXPath("count(/a/b)", xml,
				XPathConstants.NUMBER));

	}

	@Test
	public void shouldExtractXPathsFromXmlDocumentAsBoolean() {
		String xml = "<a><b>test</b><c>1</c><c>2</c></a>";
		assertEquals(Boolean.TRUE, Tools.extractXPath("count(/a/c)=2", xml,
				XPathConstants.BOOLEAN));

	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyCallerWhenXPathIsSyntacticallyIncorrect() {
		Tools.extractXPath("/a[text=1", "<a>1</a>");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyCallerWhenXmlIsWrong(){
		Tools.extractXPath("/a[text()='1']", "<a>1<a>");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotifyCallerWhenXmlCannotBeParsed(){
		Tools.extractXPath("/a[text()='1']", null);
	}

	@Test
	public void shouldConvertJsonNamedListToXmlElements() throws IOException {
		String json = "{\"announcements\":[{\"id\":1005,\"subject\":\"blahblahblah\"},{\"id\":1006,\"subject\":\"blahblahblah2\"}]}";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/announcement[2]/id[text()='1006']", xml,
				"/announcements/item[2]/id[text()='1006']", xml,
				XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertJsonLargeNamedListToXmlElements()
			throws IOException {
		String json = "{\"announcements\":[{\"xlink\":\"/announcements/1005\",\"id\":1005,\"subject\":\"This is a long announcement\",\"text\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent commodo ligula id justo laoreet ut scelerisque massa malesuada. Fusce nulla neque, lacinia eget adipiscing at, hendrerit et magna. Ut ornare ante a leo gravida viverra. Duis a odio metus. Duis mauris velit, auctor ut posuere sit amet, sollicitudin ac magna. Ut auctor sollicitudin lacus, eu lobortis urna convallis id. Phasellus lacinia dictum sem, in laoreet nunc feugiat vel. Aenean justo lacus, fringilla quis euismod sit amet, bibendum quis est. Etiam egestas, nibh quis vulputate molestie, tellus leo accumsan nisl, in commodo eros libero nec ipsum. Curabitur lacinia interdum nulla, eu mollis enim adipiscing eu. Proin semper pulvinar ante eu egestas. Nam in diam a velit elementum pretium et sagittis risus. Integer enim dui, varius vel dignissim non, aliquet vitae tellus. Donec faucibus dolor vitae sem elementum imperdiet placerat dolor condimentum. Aliquam erat volutpat. Quisque cursus lectus nisl. Nunc ut velit non odio vestibulum iaculis quis eu elit. Proin sodales mauris ut libero tempor non egestas risus laoreet.\\r\\n\",\"submitter\":\"Cam Forp\",\"startdisplaydate\":\"2010-05-17T19:00:00\",\"enddisplaydate\":\"2020-01-11T06:59:00\",\"announcementscopes\":[{\"xlink\":\"/courses/2022005\",\"scopetargetid\":2022005,\"scopetargettype\":\"course\"}]},{\"xlink\":\"/announcements/1006\",\"id\":1006,\"subject\":\"qwerty\",\"text\":\"qwerty\\r\\n\",\"submitter\":\"Cam Forp\",\"startdisplaydate\":\"2010-05-11T06:00:00\",\"enddisplaydate\":\"2011-01-11T06:59:00\",\"announcementscopes\":[{\"xlink\":\"/courses/2022005\",\"scopetargetid\":2022005,\"scopetargettype\":\"course\"}]}]}";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(
				Boolean.TRUE,
				Tools
						.extractXPath(
								// "/list/announcement[2]/announcementscopes[1]/scopetargetid[text()='2022005']",
								"/announcements/item[2]/announcementscopes/item[1]/scopetargetid[text()='2022005']",
								xml, XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertJsonNamedRootToXmlElements() throws IOException {
		String json = "{ \"resource\" : { \"name\" : \"test post\", \"data\" : \"some data\" } }";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/resource/name[text()='test post']", xml,
				XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertJsonUnnamedArrayToXmlElements() throws IOException {
		String json = "[{\"id\":1005,\"subject\":\"blahblahblah\"},{\"id\":1006,\"subject\":\"blahblahblah2\"}]";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/list/item[2]/id[text()='1006']",
				xml,
				XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertJsonUnnamedRootToXmlElements() throws IOException {
		String json = "{\"access_token\":\"mauth|79889m9rwet|2114798|2010-06-07T09%3a51%3a03|66cb32d9e0cf9ea2dad1f999946af951\",\"expires\":3600}";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/root/access_token[starts-with(text(),'mauth')]", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/root/expires[text()='3600']", xml, XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertJsonNamedValueToXmlElements() throws IOException {
		String json = "{\"access_token\":{\"name\":\"value\"}}";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/access_token/name[text()='value']", xml,
				XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertJsonValueToXmlElements() throws IOException {
		String json = "{\"access_token\":\"value\"}";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/access_token[text()='value']", xml, XPathConstants.BOOLEAN));
	}

	@Test
	public void shouldConvertHeinousJsonUnnamedListToXmlElements()
			throws IOException {
		String json = "[{\"named_list\":[{\"a\":\"a_value\"}, {\"b\":\"b_value\"}, {\"c\":\"c_value\"}]},{\"named_root\":{\"foo\":\"bar\"}},{\"a\":\"b\",\"c\":\"d\"},[{\"humpty\":1}, {\"dumpty\":2}]]";
		String xml = Tools.fromJSONtoXML(json);
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/item/named_list/a[text()='a_value']", xml,
				"/list/item[1]/named_list/item[1]/a[text()='a_value']", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/item/named_list/b[text()='b_value']", xml,
				"/list/item[1]/named_list/item[2]/b[text()='b_value']", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/item/named_list/c[text()='c_value']", xml,
				"/list/item[1]/named_list/item[3]/c[text()='c_value']", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/item[2]/named_root/foo[text()='bar']", xml,
				"/list/item[2]/named_root/foo[text()='bar']", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/list/item[3]/a[text()='b']", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				"/list/item[3]/c[text()='d']", xml, XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/item[4]/array[1]/humpty[text()='1']", xml,
				"/list/item[4]/list/item[1]/humpty[text()='1']", xml,
				XPathConstants.BOOLEAN));
		assertEquals(Boolean.TRUE, Tools.extractXPath(
				// "/list/item[4]/array[2]/dumpty[text()='2']", xml,
				"/list/item[4]/list/item[2]/dumpty[text()='2']", xml,
				XPathConstants.BOOLEAN));
	}

}
