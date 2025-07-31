<!DOCTYPE html>
<html>
<body>
    <h2>Confirmação de Atendimento</h2>
    <p>Sua atendimento foi confirmado!</p>
    <p><strong>Data/Hora Solicitada:</strong> ${confirmedDateTime}</p>
    <#if adminNotes?? && adminNotes?has_content>
    <p><strong>Notas do Administrador:</strong> ${adminNotes}</p>
    </#if>
    <p>Por favor, chegue com 10 minutos de antecedência.</p>
</body>
</html>