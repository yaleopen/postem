<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
    <div class="panel panel-info">
        <div class="panel-heading">${displayName} (<g:if test="${released == 'false'}">Released</g:if><g:else>Unreleased</g:else>)</div>
        <div class="panel-body">
            Below are your students and the feedback you provided in this file.  Use the search box below to look for a specific student.
        </div>
    </div>
    <input type="text" id="myInput" onkeyup="myFunction()" placeholder="Search...">
    <br><br>
    <a href="${createLink(action: 'index')}" class="btn btn-info" role="button">Back</a>
    <table id="myTable" data-toggle="table" data-pagination="true" data-pagination-v-align="both" data-smart-display="true" data-page-size="10" data-page-list="[5, 10, 20, 50, 100, All]">
        <thead>
            <tr>
                <g:each in="${headers}" var="header">
                    <th data-sortable="true">${header}</th>
                </g:each>
                <th data-sortable="true">Last Checked</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${contents}" var="content">
                <tr>
                    <g:set var="num" value="${1}" />
                    <g:each in="${content}" var="contentRow">
                        <g:if test="${num++ == 1}">
                            <g:set var="loginId" value="${contentRow}"/>
                            <td class="${(userActivity.any {it.loginId == loginId}) ? '' : 'danger'}">${contentRow}</td>
                        </g:if>
                        <g:else>
                            <td>${contentRow}</td>
                        </g:else>
                    </g:each>
                    <g:if test="${userActivity.any {it.loginId == loginId}}">
                        <g:each in="${userActivity}" var="activity">
                            <g:if test="${activity.loginId == loginId}">
                                <td>${activity.lastViewed}</td>
                            </g:if>
                        </g:each>
                    </g:if>
                    <g:else>
                        <td>Never</td>
                    </g:else>
                </tr>
            </g:each>

        </tbody>
    </table>
    <a href="${createLink(action: 'index')}" class="btn btn-info" role="button">Back</a>

    <script>
        function myFunction() {
            var input, filter, table, tr, td, i, j, td_val;
            input = document.getElementById("myInput");
            filter = input.value.toUpperCase();
            table = document.getElementById("myTable");
            tr = table.getElementsByTagName("tr");
            for (i = 0; i < tr.length; i++) {
                td = tr[i].getElementsByTagName("td");
                for(j = 0; j < td.length; j++){
                    td_val = td[j];
                    if (td_val) {
                        if (td_val.innerHTML.toUpperCase().indexOf(filter) > -1) {
                            tr[i].style.display = "";
                            break;
                        } else {
                            tr[i].style.display = "none";
                        }
                    }
                }
            }
        }
    </script>
</body>
</html>