import { Injectable } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FormValidationService {
  
  /**
   * Verificar si un campo es inválido
   */
  isFieldInvalid(form: FormGroup, fieldName: string): boolean {
    const field = form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  /**
   * Obtener mensaje de error para un campo
   */
  getFieldErrorMessage(form: FormGroup, fieldName: string, fieldLabel?: string): string {
    const field = form.get(fieldName);
    const label = fieldLabel || this.getFieldLabel(fieldName);
    
    if (field?.errors) {
      if (field.errors['required']) {
        return `${label} es requerido`;
      }
      if (field.errors['minlength']) {
        const minLength = field.errors['minlength'].requiredLength;
        return `${label} debe tener mínimo ${minLength} caracteres`;
      }
      if (field.errors['maxlength']) {
        const maxLength = field.errors['maxlength'].requiredLength;
        return `${label} debe tener máximo ${maxLength} caracteres`;
      }
      if (field.errors['email']) {
        return `${label} debe tener un formato válido`;
      }
      if (field.errors['pattern']) {
        return `${label} no tiene el formato correcto`;
      }
      if (field.errors['min']) {
        const min = field.errors['min'].min;
        return `${label} debe ser mayor o igual a ${min}`;
      }
      if (field.errors['max']) {
        const max = field.errors['max'].max;
        return `${label} debe ser menor o igual a ${max}`;
      }
    }
    
    return '';
  }

  /**
   * Obtener etiqueta amigable para el campo
   */
  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      'username': 'Usuario',
      'password': 'Contraseña',
      'email': 'Correo electrónico',
      'name': 'Nombre',
      'lastName': 'Apellido',
      'phone': 'Teléfono',
      'document': 'Documento',
      'confirmPassword': 'Confirmar contraseña'
    };
    
    return labels[fieldName] || fieldName.charAt(0).toUpperCase() + fieldName.slice(1);
  }

  /**
   * Marcar todos los campos como touched para mostrar errores
   */
  markAllFieldsAsTouched(form: FormGroup): void {
    Object.keys(form.controls).forEach(key => {
      const control = form.get(key);
      if (control) {
        control.markAsTouched();
        
        // Si es un FormGroup anidado, marcar recursivamente
        if (control instanceof FormGroup) {
          this.markAllFieldsAsTouched(control);
        }
      }
    });
  }

  /**
   * Verificar si el formulario tiene errores
   */
  hasFormErrors(form: FormGroup): boolean {
    return form.invalid;
  }

  /**
   * Obtener todos los errores del formulario
   */
  getFormErrors(form: FormGroup): { [key: string]: string } {
    const errors: { [key: string]: string } = {};
    
    Object.keys(form.controls).forEach(key => {
      if (this.isFieldInvalid(form, key)) {
        errors[key] = this.getFieldErrorMessage(form, key);
      }
    });
    
    return errors;
  }
}
