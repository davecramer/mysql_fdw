/**
 * Created with IntelliJ IDEA.
 * User: davec
 * Date: 13-04-29
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class Test
{
  static public void main(String []args)
  {
    def rootNode = new XmlSlurper().parseText(
            '<root><one a1="uno!"/><two>Some text!</two></root>' )

    assert rootNode.name() == 'root'
    assert rootNode.one[0].@a1 == 'uno!'
    assert rootNode.two.text() == 'Some text!'
    rootNode.children().each { assert it.name() in ['one','two'] }
  }
}
