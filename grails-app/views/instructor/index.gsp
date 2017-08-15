<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <script type="text/javascript">
        function setFileName()
        {
            var theFile = document.getElementById('myFile');
            var fileTitleText = document.getElementById('fileTitle');
            fileTitleText.value = theFile.value.split(/[\/\\]/).pop().split('.')[0];
        }
        function checkExistingFile()
        {
            var fileTitleText = document.getElementById('fileTitle');
            <g:each in="${courseFiles}" var="courseFile">
                var courseFileName = "${courseFile.displayName}";
                if(fileTitleText.value == courseFileName){
                    return confirm('Overwrite existing file?');
                }
            </g:each>
            return true;
        }
    </script>
</head>

<body>
    <div class="panel panel-info">
        <div class="panel-body">
            <dl>
                <dt>Description</dt>
                <dd class="text-info">The Post'Em Tool is a way for instructors to provide detailed text feedback to students by uploading a single CSV file.  Your CSV file can contain multiple columns of feedback and you can upload as many different CSV files as you wish.</dd>
                <dt>Instructions</dt>
                <dd class="text-info">a) To begin, download your course template CSV file below.  Your template will contain all of the required columns and is pre-populated with your student roster.  Add your data/feedback/grades in the columns to the right of the required columns.  Make sure that you put the feedback for each student in the appropriate row and column.</dd>
                <dd class="text-info">b) When you are ready, save and upload your completed template file below.</dd>
            </dl>
        </div>
    </div>
            <div class="form-group">
                <g:if test="${status == 'error'}">
                    <g:if test="${badUsers && badUsers.size() > 0}">
                        <div class="alert alert-danger alert-dismissable">
                            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                            <strong>Error - Invalid Users: Please correct the below rows and try again.</strong><br>
                            <ul style="padding-left:20px">
                                <g:each in="${badUsers}" var="badUser">
                                    <li>${badUser}</li>
                                </g:each>
                            </ul>
                        </div>
                    </g:if>
                    <g:else>
                        <div class="alert alert-danger alert-dismissable">
                            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                            <strong><g:message code="${description}"/></strong><br>
                        </div>
                    </g:else>
                </g:if>
                <g:elseif test="${status == 'success'}">
                    <div class="alert alert-success alert-dismissable">
                        <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                        <strong>Success!</strong><br>
                    </div>
                </g:elseif>
                <label>Add/Update Feedback</label><br>
                <g:link controller="instructor" action="downloadFile">Download your course template</g:link><br>
                <g:uploadForm controller="instructor" action="upload" onsubmit="return checkExistingFile()">
            </div>
            <div class="form-group">
                <label for="myFile">Postem CSV</label>
                <input type="file" class="form-control-file" aria-describedby="fileHelp" name="myFile" id="myFile" onchange="setFileName()"/>
                <small id="fileHelp" class="form-text text-muted">File with extension *.csv based on course template. File Size Limit = 10 MB</small>
            </div>
            <div class="form-group">
                <label for="fileTitle">Title</label><br>
                <g:textField id="fileTitle" name="fileTitle"/>
            </div>
            <div class="form-group">
                <label class="form-check-label">
                    <g:checkBox name="releaseCheckbox" value="${false}" />
                    Release feedback to participants?
                </label>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Upload</button>
            </div>
        </g:uploadForm>
    <table class="table table-hover">
        <tr>
            <th>Title</th>
            <th>Modified By</th>
            <th>Last Modified</th>
            <th>Released</th>
            <th></th>
        </tr>
        <g:each in="${courseFiles}" var="courseFile">
            <tr>
                <td>${courseFile.displayName}</td>
                <td>${courseFile.modifiedBy}</td>
                <td>${courseFile.updatedAt}</td>
                <td>
                    <g:if test="${courseFile.hidden == true}">
                    Yes
                    </g:if>
                    <g:else>
                    No
                    </g:else>
                </td>
                <td>
                    <div class="btn-group">
                        <button type="button" class="btn btn-danger">Actions</button>
                        <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
                            <span class="caret"></span>
                            <span class="sr-only">Toggle Dropdown</span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="${createLink(controller: 'instructor', action: 'viewFile', params: [fileId: courseFile.fileId, fileURL: courseFile.url, displayName: courseFile.displayName, released: !courseFile.hidden])}">View</a></li>
                            <li><a href="${createLink(controller: 'instructor', action: 'editFile', params: [fileId: courseFile.fileId, editType: 'rename', displayName: courseFile.displayName])}">Edit Title</a></li>
                            <li><a href="${createLink(controller: 'instructor', action: 'editFile', params: [fileId: courseFile.fileId, editType: 'add', displayName: courseFile.displayName])}">Upload New Version</a></li>
                            <li><a href="${createLink(controller: 'instructor', action: 'downloadCSV', params: [fileId: courseFile.fileId, fileURL: courseFile.url, displayName: courseFile.displayName])}">Download</a></li>
                            <g:if test="${courseFile.hidden == true}">
                                <li><a href="${createLink(controller: 'instructor', action: 'unrelease', params: [fileId: courseFile.fileId])}">Unrelease</a></li>
                            </g:if>
                            <g:else>
                                <li><a href="${createLink(controller: 'instructor', action: 'release', params: [fileId: courseFile.fileId])}">Release</a></li>
                            </g:else>
                            <li class="divider"></li>
                            <li><g:link controller="instructor" action="delete" params="[fileId: courseFile.fileId]" onclick="return confirm('Are you sure you want to delete?');">Delete</g:link></li>
                        </ul>
                    </div>
                </td>
            </tr>
        </g:each>
    </table>
</body>
</html>