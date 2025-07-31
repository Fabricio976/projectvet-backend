<html>
<body>
    <h2>Rejeição de Atendimento</h2>
    <p>Infelizmente, sua solicitação não vai poder ser atendida.</p>
    <#if adminNotes?? && adminNotes?has_content>
    <p><strong>Motivo:</strong> ${adminNotes}</p>
    </#if>
    <p>Por favor, tente agendar uma nova data.</p>
</body>
</html>