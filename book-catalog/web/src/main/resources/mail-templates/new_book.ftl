<!DOCTYPE html>
<html lang="uk"><head><meta charset="UTF-8"><title>Нова книга</title></head>
<body style="margin:0;padding:24px;background:#f1f5f9;font-family:Arial,sans-serif;color:#17233e;">
  <div style="max-width:620px;margin:auto;background:#fff;border-radius:16px;padding:28px;border:1px solid #d8e0ee;">
    <#if logoUrl?has_content>
      <img src="${logoUrl?html}" alt="Book Catalog" width="150" style="max-width:150px;margin-bottom:18px;display:block;">
    <#else>
      <div style="font-size:23px;font-weight:bold;color:#233b69;margin-bottom:18px;">📚 Book Catalog</div>
    </#if>
    <h1 style="font-size:26px;margin:0 0 12px;color:#1b2d52;">До каталогу додано нову книгу!</h1>
    <p style="color:#566580;">Вітаємо! У бібліотеці з’явилося нове видання.</p>
    <div style="background:#f5f8ff;border-left:5px solid #3559a3;padding:18px 22px;border-radius:8px;margin:20px 0;">
      <h2 style="font-size:21px;margin:0 0 12px;">${title?html}</h2>
      <p style="margin:6px 0;"><b>Автор:</b> ${author?html}</p>
      <p style="margin:6px 0;"><b>Рік видання:</b> ${year?c}</p>
      <p style="margin:6px 0;"><b>Додано:</b> ${added?html}</p>
    </div>
    <#if year < 2000>
      <div style="padding:12px 16px;background:#fff4d7;border:1px solid #e5bd64;border-radius:8px;color:#925b00;font-weight:bold;">★ Раритетне видання!</div>
    </#if>
    <p style="font-size:13px;color:#64748b;margin-top:28px;border-top:1px solid #e2e8f0;padding-top:16px;">BookApp © 2026 · Навчальний проєкт Spring MVC + FreeMarker</p>
  </div>
</body></html>
