<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
    <table id="myTable" class="table table-hover">
        <tr>
            <g:each in="${headers}" var="header">
                <th>${header}</th>
            </g:each>
        </tr>
        <g:each in="${contents}" var="content">
            <tr>
                <g:each in="${content}" var="contentRow">
                    <td>${contentRow}</td>
                </g:each>
            </tr>
        </g:each>
    </table>
    <a href="${createLink(action: 'index')}" class="btn btn-info" role="button">Back</a>
</body>
</html>