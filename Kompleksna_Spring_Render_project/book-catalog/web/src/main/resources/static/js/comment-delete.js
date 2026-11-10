for (const form of document.querySelectorAll(".comment-delete")) {
    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const button = form.querySelector("button");
        const error = form.querySelector('[role="alert"]');
        error.hidden = true;
        button.disabled = true;
        try {
            const response = await fetch(form.action, {
                method: "POST",
                body: new URLSearchParams(new FormData(form)),
                headers: { Accept: "application/json" }
            });
            if (response.status === 204) {
                window.location.reload();
                return;
            }
            const result = response.headers.get("content-type")?.includes("application/json")
                ? await response.json() : {};
            error.textContent = result.error || "Не вдалося видалити коментар. Оновіть сторінку та перевірте вхід.";
        } catch {
            error.textContent = "Не вдалося зв’язатися із сервером.";
        } finally {
            button.disabled = false;
        }
        error.hidden = false;
    });
}
