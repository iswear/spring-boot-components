<#list rows as row>
  <Row>
      <#list row.cells as cell>
          <Cell><Data ss:Type="String">${cell}</Data></Cell>
      </#list>
  </Row>
</#list>