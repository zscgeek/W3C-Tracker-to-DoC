

def today= new Date() //represents the date and time when it is created


def ir = new XmlSlurper().parse(args[0])

def allIssues = ir.issues.issue
def ccxmlIssues = ir.issues.issue.findAll{it.product.text()=="2"}
def publicIssues = ir.issues.issue.findAll{it.product.text()=="2" && it.title.text().contains('PUBLIC')}
def lcwdIssues = ir.issues.issue.findAll{it.product.text()=="2" && it.title.text().contains('LCWD')}
def pubOrlcwdIssues = ir.issues.issue.findAll {
            it.product.text()=="2" && 
            (it.title.text().contains('LCWD') || it.title.text().contains('PUBLIC'))
}
def issueSet = pubOrlcwdIssues

StringWriter writer = new java.io.StringWriter()

def build = new groovy.xml.MarkupBuilder(writer)
build.html {
head {
    
    
    title "CCXML 1.0: Candidate Recommendation Disposition of Comments"

    link( rel:"stylesheet", type:"text/css",href:"http://www.w3.org/StyleSheets/general.css")
    style(type:"text/css", '''  
    .indent {  
        margin: 30px;  
        background-color: AliceBlue   
    }  
    
    .NA {  
    }  
    .ACCEPTED {  
        background-color: Aquamarine 
    }  
    .TEXTSUPERSEDED {  
        background-color: LightSkyBlue  
    }  
    .CLARIFICATION {  
        background-color: DarkSeaGreen   
    }  
    .DROPPED  {  
        background-color: Yellow    
    }  
    .REASSIGNEDID {  
        background-color: Gainsboro 
    }  
    ''')  

}
body(bgcolor: '#ffffff') {

div class:"head", {
    p {
        a href:"http://www.w3.org" {
            img width:"72", height:"48", alt:"W3C", src:"http://www.w3.org/Icons/w3c_home"
        }
        a href:"",""
    }
    
    h1 id:"title", style:"text-align: center", 'CCXML 1.0:Candidate Recommendation Disposition of Comments'
    
    dl {
        dt "This version"
        dd "${today}"
        dt "Editor"
        dd "RJ Auburn, Voxeo"
    }
    
}

h2 "Abstract"
p {
    mkp.yield 'This document details the responses made by the Voice Browser '
    mkp.yield 'Working Group to issues raised during the '
    a href:"http://www.w3.org/2004/02/Process-20040205/tr.html#cfi","Candidate Recommendation"
    mkp.yield ' period (beginning XXX and ending XXX).'
    a href:"mailto:www-voice-request@w3.org","www-voice-request@w3.org"
    mkp.yield "("
    a href:"http://lists.w3.org/Archives/Public/www-voice/", "archive"
    mkp.yield ") mailing list."
}

h2 "Status"
p {
    mkp.yield 'This document of the W3C\'s Voice Browser Working Group describes the disposition '
    mkp.yield 'of comments as of XXXX on the '
    a href:"http://www.w3.org/TR/2007/WD-ccxml-20070119//", "Last Call Working Draft Voice Browser Call Control XML (CCXML) Version 1.0."
    mkp.yield 'It may be updated, replaced or rendered obsolete by other W3C documents at any time.'    
}

p {
    mkp.yield 'For background on this work, please see the '
    a href:"http://www.w3.org/Voice/Activity", "Voice Browser Activity Statement."
}



h2 "Comment summary"
table border:"1", { 
    tr {
        th "ID"
        th "Title"
        th "Date Opened"
        th "Last Updated"
        th "Result"
    }
    for ( issue in issueSet ) {
        tr {
            td {
                a 'href':"#ISSUE-${issue.id}", "ISSUE-${issue.id}"
            }
            td "${issue.title}"
            td "${issue.created}"
        
        
            def lastUpdate = "N/A"
            def hov = ""
            for ( email in issue.emails.email ) {
                if (
                    email.subject.toString().toLowerCase().indexOf("disposition of comments") == -1 &&
                    email.subject.toString().indexOf("DoC") == -1 
                   ) {
                    lastUpdate =  "${new java.util.Date(Long.parseLong(email.timestamp.toString()) * 1000).format('yyyy-MM-dd')}"   
                     hov =  "${email.subject}"   
                     break
                }
            }
            td 'title':"${hov}", "${lastUpdate}"

                def z = issue.notes.note.size()

                def result = "NA"
                if (z >= 1) {
                    z = z-1
                    if (issue.notes.note.getAt(z).description.toString().startsWith("RESULT=") ) {
                        result = "${issue.notes.note.getAt(z).description.toString().substring(7)}"
                    }
                  
                }
                td 'class':"${result}", "${result}"
        }
    }
    
}

// 
// Dump local info about each issue. 
// 

h2 "Issue detail"
for ( issue in issueSet ) {
    hr {}
    h3 'id':"ISSUE-${issue.id}",{
            a 'href':"http://www.w3.org/Voice/Group/track/issues/${issue.id}?changelog", "ISSUE-${issue.id}"
            mkp.yield " - ${issue.title}"
    }
    h4 "Opened: ${issue.created}"

    def lastUpdate = "N/A"
    for ( note in issue.notes.note ) {
     lastUpdate =  "${new java.util.Date(Long.parseLong(note.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}"   
    }
    h4 "Last Updated: ${lastUpdate}"
    
    h4 "State: ${issue.state}"
    h4 "Description:"
    pre 'class':'indent', "${issue.description}"

    h4 "Notes:"
    for ( note in issue.notes.note ) {
        ul {
            li {
             b "${new java.util.Date(Long.parseLong(note.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}: "   
                 // mkp.yield "${note.description}"   
             pre "${note.description}"   
            }
        }
    }

    h4 "Related e-mails:"
    def stack = new ArrayList()
    for ( email in issue.emails.email ) {
        stack.add(0, email)
    }
    for ( email in stack ) {
        ul {
            li {
                b "${new java.util.Date(Long.parseLong(email.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}: "   
                a 'href':"${email.link}", "${email.subject}"
            }
        }
    }


}



}
}
print writer;
