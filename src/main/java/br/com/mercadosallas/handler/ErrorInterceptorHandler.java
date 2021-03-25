package br.com.mercadosallas.handler;

import br.com.mercadosallas.clientes.exception.dto.ErroDto;
import br.com.mercadosallas.clientes.exception.dto.ErroFormularioDto;
import br.com.mercadosallas.clientes.exception.exceptions.ClienteNotFoundException;
import br.com.mercadosallas.clientes.exception.exceptions.CpfAlreadyExistsException;
import br.com.mercadosallas.clientes.exception.exceptions.EmailAlreadyExistsException;
import br.com.mercadosallas.clientes.exception.exceptions.InvalidEmailException;
import br.com.mercadosallas.telefones.exception.MaximoTelefoneException;
import br.com.mercadosallas.telefones.exception.MinimoTelefoneException;
import br.com.mercadosallas.telefones.exception.TelefoneNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorInterceptorHandler {

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ErroFormularioDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ErroFormularioDto> errosForm = new ArrayList<>();

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        fieldErrors.forEach(fieldError -> {
            String mensagem = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());

            ErroFormularioDto erroForm = new ErroFormularioDto(fieldError.getField(), mensagem);

            errosForm.add(erroForm);
        });

        return errosForm;
    }

    @ExceptionHandler(ClienteExceptionGeneric.class)
    public ResponseEntity<ErroDto> handleExceptionGeneric(ClienteExceptionGeneric e) {
        return new ResponseEntity<>(new ErroDto(e.getMessage(), e.getHttpStatus().value(), e.getHttpStatus().getReasonPhrase()), e.getHttpStatus());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeException.class)
    public String handleDateTimeException(DateTimeException exception) {
        return "Ocorreu um erro ao formatar a data";
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErroDto handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return new ErroDto("Ocorreu um erro no tratamento dos dados. Verifique os dados preenchidos ou tente novamente mais tarde.");
    }

}
