<!DOCTYPE html>
<html>
<body>
    <h2>Confirmação de Consulta</h2>
    <p>Sua consulta foi confirmada!</p>
    <p><strong>Data/Hora Confirmada:</strong> ${confirmedDateTime}</p>
    <#if adminNotes?has_content>
        <p><strong>Notas do Administrador:</strong> ${adminNotes}</p>
    </#if>
    <p>Por favor, chegue com 10 minutos de antecedência.</p>
</body>
</html>