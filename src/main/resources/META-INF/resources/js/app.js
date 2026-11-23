/* Portal de Vagas — JS de UI (máscaras, confirmação, busca) */

(function () {
  "use strict";

  // Máscara CPF: 000.000.000-00
  function maskCpf(value) {
    value = value.replace(/\D/g, "").slice(0, 11);
    value = value.replace(/(\d{3})(\d)/, "$1.$2");
    value = value.replace(/(\d{3})(\d)/, "$1.$2");
    value = value.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
    return value;
  }

  // Máscara telefone: (00) 00000-0000
  function maskPhone(value) {
    value = value.replace(/\D/g, "").slice(0, 11);
    if (value.length > 10) {
      value = value.replace(/(\d{2})(\d{5})(\d{0,4})/, "($1) $2-$3");
    } else if (value.length > 6) {
      value = value.replace(/(\d{2})(\d{4})(\d{0,4})/, "($1) $2-$3");
    } else if (value.length > 2) {
      value = value.replace(/(\d{2})(\d{0,5})/, "($1) $2");
    }
    return value;
  }

  function bindMask(selector, maskFn) {
    document.querySelectorAll(selector).forEach(function (el) {
      el.addEventListener("input", function () {
        var start = el.selectionStart;
        var oldLen = el.value.length;
        el.value = maskFn(el.value);
        var newLen = el.value.length;
        el.setSelectionRange(start + (newLen - oldLen), start + (newLen - oldLen));
      });
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    bindMask("[data-mask='cpf']", maskCpf);
    bindMask("[data-mask='phone']", maskPhone);

    // Confirmação de exclusão
    document.querySelectorAll("[data-confirm]").forEach(function (el) {
      el.addEventListener("submit", function (e) {
        var msg = el.getAttribute("data-confirm") || "Confirmar esta ação?";
        if (!window.confirm(msg)) {
          e.preventDefault();
        }
      });
    });

    // Auto-dismiss alerts após 5s
    document.querySelectorAll(".alert").forEach(function (alert) {
      setTimeout(function () {
        alert.style.transition = "opacity .4s";
        alert.style.opacity = "0";
        setTimeout(function () { alert.remove(); }, 400);
      }, 5000);
    });

    // Foco na busca de vagas se existir
    var search = document.getElementById("busca-vagas");
    if (search && window.location.pathname === "/vagas" && !search.value) {
      // não força foco automático em mobile
    }

    // Contador de resultados na busca
    var results = document.querySelectorAll(".job-card");
    var counter = document.getElementById("result-count");
    if (counter) {
      counter.textContent = results.length + (results.length === 1 ? " vaga encontrada" : " vagas encontradas");
    }
  });
})();
