-- Um schema por microserviço (cada serviço é dono dos seus dados)
CREATE DATABASE IF NOT EXISTS clinical_administrativo;
CREATE DATABASE IF NOT EXISTS clinical_agendamento;
GRANT ALL PRIVILEGES ON clinical_administrativo.* TO 'clinical'@'%';
GRANT ALL PRIVILEGES ON clinical_agendamento.* TO 'clinical'@'%';
FLUSH PRIVILEGES;
