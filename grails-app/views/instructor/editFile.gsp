<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
    <div class="form-group">
        <g:if test="${editType == 'rename'}">
            <g:form action="renameFile">
                <br>
                <label style="display: inline-block; float: left; clear: left; width: 300px; text-align: right">Current Title: <g:field type="text" name="currentName" disabled="true" value="${displayName}"/></label><br>
                <label style="display: inline-block; float: left; clear: left; width: 300px; text-align: right">New Title:     <g:textField name="fileName"/></label><br><br>
                <g:hiddenField name="fileId" value="${fileId}" />
                <input style="margin-left: 230px" type="submit" class="btn btn-primary" value="Submit">
            </g:form>
        </g:if>
        <g:elseif test="${editType == 'add'}">
            <div class="alert alert-warning alert-dismissable">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                <strong>Warning: </strong>This file will overwrite and replace the existing file. Previous versions will not be saved.<br>
            </div>
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
            <label>Add/Update Feedback</label><br>
            <g:link action="downloadFile">Download your course template</g:link><br>
            <g:uploadForm controller="instructor" action="uploadNewVersion">
                </div>
                <div class="form-group">
                    <label for="myFile">Postem CSV</label>
                    <input type="file" class="form-control-file" aria-describedby="fileHelp" name="myFile" id="myFile"/>
                    <small id="fileHelp" class="form-text text-muted">File with extension *.csv based on course template. File Size Limit = 10 MB<</small>
                </div>
                <g:hiddenField name="fileTitle" value="${displayName}" />
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
        </g:elseif>
        <br>
        <a href="${createLink(action: 'index')}" class="btn btn-info" role="button">Back</a>
    </div>
</body>
</html>