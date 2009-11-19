

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.ContentType.TEXT


def checkPublic(url){
    // return true
    // create a new builder
    def http = new HTTPBuilder( url )

    http.request( HEAD, TEXT ) {req ->
     response.success = { resp ->
         def loc = resp.getFirstHeader('Location')
         if (loc != null && loc != "") {
             if (loc.toString().indexOf("Archives/Member") == -1) 
             {
                 return true
             } else {
                 return false
             }
         } else {
             return true
         }
     }
     response.failure = { resp ->
         return false
     }
    }
}


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
    
    
    
    title "CCXML 1.0: Last Call Working Draft Disposition of Comments"

    link( rel:"stylesheet", type:"text/css",href:"http://www.w3.org/StyleSheets/general.css")
    style(type:"text/css", '''  
    .indent {  
        margin: 30px;  
    }  
    .indentpre {  
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
    
    h1 id:"title", style:"text-align: center", 'CCXML 1.0: Last Call Working Draft Disposition of Comments'
    
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
    a href:"http://www.w3.org/2004/02/Process-20040205/tr.html#cfi","Last Call Working Draft"
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
p "Legend:" 
table border:"1", { 
    tr {
        td 'class':"ACCEPTED", "ACCEPTED"
        td 'class':"ACCEPTED", "Comment was accepted"
    }
    tr {
        td 'class':"TEXTSUPERSEDED", "TEXTSUPERSEDED"
        td 'class':"TEXTSUPERSEDED", "Text that was commented on had already been changed."
    }
    tr {
        td 'class':"CLARIFICATION", "CLARIFICATION"
        td 'class':"CLARIFICATION", "Comment only required a clarification."
    }
    tr {
        td 'class':"DROPPED", "DROPPED"
        td 'class':"DROPPED", "Feature in question was removed from the spec."
    }
    tr {
        td 'class':"REASSIGNEDID", "REASSIGNEDID"
        td 'class':"REASSIGNEDID", "Issue number was changed to a new ID"
    }
}
p "Results:" 
table border:"1", { 
    tr {
        th "ID"
        th "Title"
        th "Date Opened"
        th "Last Updated"
        th "Disposition"
        th "Acceptance"
        th "Related Issues"
    }
    for ( issue in issueSet ) {
        tr {
            //
            // Link to issue detail
            //
            td {
                a 'href':"#ISSUE-${issue.id}", "ISSUE-${issue.id}"
            }

            //
            // Issue title
            //
            td "${issue.title}"

            //
            // Issue creation date
            //
            td "${issue.created}"
        
        
            //
            // Get the date of the last email
            //
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
            
            //
            // Get the disposition and acceptance type of the comment
            //
            def c = issue.notes.note.size()
            def result = "NA"
            def acceptance = "NA"
            def related = "NONE"
            for (note in issue.notes.note) {
                if (note.description.toString().startsWith("RESULT=") ) {
                    result = "${note.description.toString().substring(7)}"
                }
                if (note.description.toString().startsWith("ACCEPTANCE=") ) {
                    acceptance = "${note.description.toString().substring(11)}"
                }
                if (note.description.toString().startsWith("RELATED=") ) {
                    related = "${note.description.toString().substring(8)}"
                }
            }
            td 'class':"${result}", "${result}"
            td 'class':"${acceptance}", "${acceptance}"
            td "${related}"
        }
    }
    
}

// 
// Dump local info about each issue. 
// 

h2 "Issue detail"
for ( issue in issueSet ) {
    hr {}
    h3 'id':"ISSUE-${issue.id}", "ISSUE-${issue.id} - ${issue.title}"

    h4 "Tracker (W3C Member only):"
    a class:'indent','href':"http://www.w3.org/Voice/Group/track/issues/${issue.id}?changelog", "ISSUE-${issue.id}"

    h4 "Opened: ${issue.created}"

    def lastUpdate = "N/A"
    for ( note in issue.notes.note ) {
     lastUpdate =  "${new java.util.Date(Long.parseLong(note.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}"   
    }
    h4 "Last Updated: ${lastUpdate}"
    
    h4 "State: ${issue.state}"

    h4 "Description:"
    pre 'class':'indentpre', "${issue.description}"

    h4 "Notes:"
    for ( note in issue.notes.note ) {
        ul {
            li {
             b "${new java.util.Date(Long.parseLong(note.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}: "   
             pre "${note.description}"   
            }
        }
    }

    h4 "Related e-mails:"
    // emails are in reverse order (sigh)
    // this will reverse them. 
    def stack = new ArrayList()
    for ( email in issue.emails.email ) {
        stack.add(0, email)
    }
    ul {    
        for ( email in stack ) {
            if (
                email.subject.toString().toLowerCase().indexOf("disposition of comments") == -1 &&
                email.subject.toString().indexOf("DoC") == -1 
               ) 
             {
               li {
                   b "${new java.util.Date(Long.parseLong(email.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}: "   
                   if (checkPublic(email.link)) {
                       a 'href':"${email.link}", "${email.subject}"
                   } else {
                       a 'href':"${email.link}", "${email.subject} - [members only]"
                       
                   }
               }
            }
        }
    }


}



}
}
print writer;
