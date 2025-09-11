package com.filipecode.papertrading.infrastructure.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return true; // A anotação @NotBlank já vai cuidar de CPFs nulos e em branco
        }
        // Remove caracteres não numéricos
        String cleanedCpf = cpf.replaceAll("[^\\d]", "");

        // Verificar se o CPF limpo tem 11 dígitos.
        if (cleanedCpf.length() != 11) {
            return false;
        }

        // Verificar se não é uma sequência de dígitos repetidos (ex: 111.111.111-11).
        String regex = "^(\\d)\\1{10}$";
        // ^ → início da string
        // (\\d) → captura um dígito qualquer de 0 a 9
        // \\1{10} → repete o mesmo dígito capturado mais 10 vezes (totalizando 11 dígitos iguais)
        // $ → fim da string
        if (cleanedCpf.matches(regex)) {
            return false;
        }
        // Todo 3. Calcular o primeiro dígito verificador.
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int multiNumber = 10 - i;
            sum += Character.getNumericValue(cleanedCpf.charAt(i)) * multiNumber;
        }
        int firstDigit = 11 - (sum % 11);

        if (firstDigit > 9) {
            firstDigit = 0;
        }
        if (Character.getNumericValue(cleanedCpf.charAt(9)) != firstDigit) {
            return false;
        }
        // Todo 4. Calcular o segundo dígito verificador.
        sum = 0;
        for (int i = 0; i < 10; i++) {
            int multiNumber = 11 - i;
            sum += Character.getNumericValue(cleanedCpf.charAt(i)) * multiNumber;
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) {
            secondDigit = 0;
        }
        if (Character.getNumericValue(cleanedCpf.charAt(10)) != secondDigit) {
            return false;
        }

        return true;

    }
}
