

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
title 'CCXML Disposition of Comments'

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

h1 'CCXML Disposition of comments'
p "Generated: ${today} by DoC Bot v0.1 - RJ Auburn"

// 
// Dump some basic stats 
// 
h1 "CCXML Tracker stats"
table border:"1", { 
    tr {
        td "Issue database size:"
        td "${allIssues.size()}"
    }
    tr {
        td "CCXML issue count:"
        td "${ccxmlIssues.size()}"
    }
    tr {
        td "Public CCXML issue count: "
        td "${publicIssues.size()}"
    }
    tr {
        td "LCWD CCXML issue count:"
        td "${lcwdIssues.size()}"
    }
    tr {
        td "LCWD or PUB CCXML issue count:"
        td "${pubOrlcwdIssues.size()}"
    }
}

// 
// Create the  top level issue table. 
// 
h1 "Comment summery"
table border:"1", { 
    tr {
        th "ID"
        th "Title"
        th "Date Opened"
        th "Last Updated"
        th "Result"
        th "tracker"
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

                td {
                    a 'href':"http://www.w3.org/Voice/Group/track/issues/${issue.id}?changelog", "ISSUE-${issue.id}"
                }

                
            
        }
    }
    
}

// 
// Dump local info about each issue. 
// 

h1 "Issue detail"
for ( issue in issueSet ) {
    hr {}
    h2 'id':"ISSUE-${issue.id}",{
            a 'href':"http://www.w3.org/Voice/Group/track/issues/${issue.id}?changelog", "ISSUE-${issue.id}"
            mkp.yield " - ${issue.title}"
    }
    h3 "Opened: ${issue.created}"

    def lastUpdate = "N/A"
    for ( note in issue.notes.note ) {
     lastUpdate =  "${new java.util.Date(Long.parseLong(note.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}"   
    }
    h3 "Last Updated: ${lastUpdate}"
    
    h3 "State: ${issue.state}"
    h3 "Description:"
    pre 'class':'indent', "${issue.description}"

    h3 "Notes:"
    for ( note in issue.notes.note ) {
        ul {
            li {
             b "${new java.util.Date(Long.parseLong(note.timestamp.toString()) * 1000).format('yyyy-MM-dd hh:mm')}: "   
                 // mkp.yield "${note.description}"   
             pre "${note.description}"   
            }
        }
    }

    h3 "Related e-mails:"
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
