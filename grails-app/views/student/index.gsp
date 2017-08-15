<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<table class="table table-hover">
    <tr>
        <th>Title</th>
        <th>Last Modified</th>
        <th></th>
    </tr>
    <g:each in="${courseFiles}" var="courseFile">
        <tr>
            <td>${courseFile.displayName}</td>
            <td>${courseFile.updatedAt}</td>
            <td>
                <div class="btn-group">
                    <button type="button" class="btn btn-danger">Actions</button>
                    <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
                        <span class="caret"></span>
                        <span class="sr-only">Toggle Dropdown</span>
                    </button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="${createLink(action: 'viewFile', params: [fileURL: courseFile.url, fileId: courseFile.fileId])}">View</a></li>
                    </ul>
                </div>
            </td>
        </tr>
    </g:each>
</table>
</body>
</html>