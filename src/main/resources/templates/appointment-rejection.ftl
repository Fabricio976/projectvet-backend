<html>
<body>
    <h2>Rejeição de Consulta</h2>
    <p>Infelizmente, sua solicitação de consulta não pôde ser confirmada.</p>
    <#if adminNotes?has_content>
        <p><strong>Motivo:</strong> ${adminNotes}</p>
    </#if>
    <p>Por favor, tente agendar uma nova data.</p>
</body>
</html>