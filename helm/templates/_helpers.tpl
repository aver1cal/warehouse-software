{{- define "warehouse-backend.name" -}}
warehouse-backend
{{- end }}

{{- define "warehouse-backend.fullname" -}}
{{ .Release.Name }}-warehouse-backend
{{- end }}