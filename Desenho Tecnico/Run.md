# 🚀 Guia de Execução - Sistema de Inventário

## 📋 **Pré-requisitos**

Antes de executar o projeto, certifique-se de ter instalado:

- ☕ **Java 17 ou superior** ([Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.org/))
- 📦 **Maven 3.6 ou superior** ([Download Maven](https://maven.apache.org/download.cgi))
- 💻 **IDE** (IntelliJ IDEA ou Eclipse) ou terminal

### **Verificar Instalação:**
```bash
# Verificar Java
java -version
# Deve mostrar: openjdk version "17.x.x" ou superior

# Verificar Maven  
mvn -version
# Deve mostrar: Apache Maven 3.6.x ou superior
```

---

## 🧠 **Executando no IntelliJ IDEA**

### **Método 1: Importar como Projeto Maven (Recomendado)**

#### **Passo 1: Abrir o Projeto**
1. Abra o IntelliJ IDEA
2. Clique **"Open"** ou **"File → Open"**
3. Navegue até a pasta `/app/backend`
4. Selecione o arquivo **`pom.xml`**
5. Clique **"Open as Project"**

#### **Passo 2: Aguardar Sincronização**
1. IntelliJ detectará automaticamente que é um projeto Maven
2. Aguarde o **"Importing..."** na barra de status
3. Maven baixará todas as dependências automaticamente
4. Processo pode levar 2-5 minutos na primeira vez

#### **Passo 3: Configurar SDK do Projeto**
1. Vá em **"File → Project Structure"** (Ctrl+Alt+Shift+S)
2. Em **"Project"**, configure:
   - **Project SDK:** Java 17 (ou superior)
   - **Project language level:** SDK default
3. Clique **"Apply"** → **"OK"**

#### **Passo 4: Executar a Aplicação**
1. Navegue até: `src/main/java/com/inventory/InventorySystemApplication.java`
2. **Opção A:** Clique no ícone ▶️ verde ao lado da classe
3. **Opção B:** Clique com botão direito → **"Run 'InventorySystemApplication'"**
4. **Opção C:** Use o atalho **Ctrl+Shift+F10**

#### **Passo 5: Verificar Execução**
- Console deve mostrar logs do Spring Boot
- Aguarde a mensagem: `Started InventorySystemApplication in X.XX seconds`
- Aplicação estará rodando em: http://localhost:8080

### **Método 2: Via Maven Tool Window**

#### **Passo 1: Abrir Maven Tool Window**
1. **"View → Tool Windows → Maven"** ou clique na aba **"Maven"** (lateral direita)

#### **Passo 2: Executar via Maven**
1. Expanda: **inventory-system → Plugins → spring-boot**
2. Duplo clique em: **spring-boot:run**
3. Ou execute: **inventory-system → Lifecycle → compile** primeiro

### **Configurações Adicionais do IntelliJ:**

#### **Hot Reload (Desenvolvimento):**
1. **"File → Settings"** (Ctrl+Alt+S)
2. **"Build → Compiler"**
3. Marque: ☑️ **"Build project automatically"**
4. **"Advanced Settings"**
5. Marque: ☑️ **"Allow auto-make to start even if developed application is currently running"**

#### **Configurar Run Configuration:**
1. **"Run → Edit Configurations"**
2. **"+ → Spring Boot"**
3. Configure:
   - **Name:** Inventory System
   - **Main class:** `com.inventory.InventorySystemApplication`
   - **Working directory:** `/app/backend`
   - **JRE:** Java 17

---

## 🌙 **Executando no Eclipse**

### **Método 1: Importar como Projeto Maven Existente**

#### **Passo 1: Importar Projeto**
1. Abra o Eclipse
2. **"File → Import"**
3. Selecione: **"Maven → Existing Maven Projects"**
4. Clique **"Next"**

#### **Passo 2: Selecionar Diretório**
1. **"Root Directory"**: Navegue até `/app/backend`
2. Eclipse detectará automaticamente o `pom.xml`
3. Certifique-se que o projeto está selecionado ☑️
4. Clique **"Finish"**

#### **Passo 3: Aguardar Sincronização**
1. Eclipse baixará dependências automaticamente
2. Progresso será mostrado na barra de status
3. Aguarde conclusão (pode levar alguns minutos)

#### **Passo 4: Configurar Java Build Path**
1. Clique direito no projeto → **"Properties"**
2. **"Java Build Path → Libraries"**
3. Certifique-se que **"Modulepath"** ou **"Classpath"** contém:
   - **JRE System Library [JavaSE-17]**
   - **Maven Dependencies**

#### **Passo 5: Executar a Aplicação**
1. Navegue até: `src/main/java → com.inventory → InventorySystemApplication.java`
2. Clique direito na classe → **"Run As → Java Application"**
3. Ou use atalho: **Ctrl+F11**

### **Método 2: Via Maven (Eclipse)**

#### **Passo 1: Maven Build**
1. Clique direito no projeto → **"Run As → Maven build..."**
2. **Goals:** `spring-boot:run`
3. Clique **"Run"**

### **Configurações Adicionais do Eclipse:**

#### **Configurar Server (Opcional):**
1. **"Window → Show View → Servers"**
2. Clique direito na aba **"Servers → New → Server"**
3. Selecione um servidor apropriado (se necessário)

#### **Auto Refresh:**
1. **"Window → Preferences"**
2. **"General → Workspace"**
3. Marque: ☑️ **"Refresh using native hooks or polling"**

---

## 💻 **Executando via Terminal/Linha de Comando**

### **Método 1: Maven Spring Boot Plugin (Recomendado)**

#### **Passo 1: Navegar até o Diretório**
```bash
cd /app/backend
```

#### **Passo 2: Compilar o Projeto**
```bash
mvn clean compile
```

**Saída esperada:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

#### **Passo 3: Executar a Aplicação**
```bash
mvn spring-boot:run
```

**Saída esperada:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v3.2.0)

Started InventorySystemApplication in 2.847 seconds
```

### **Método 2: Executar JAR Compilado**

#### **Passo 1: Gerar JAR**
```bash
cd /app/backend
mvn clean package
```

#### **Passo 2: Executar JAR**
```bash
java -jar target/inventory-system-1.0.0.jar
```

### **Método 3: Desenvolvimento com Auto-Reload**
```bash
cd /app/backend
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### **Comandos Úteis:**

#### **Executar Testes:**
```bash
mvn test
```

#### **Compilar sem Executar Testes:**
```bash
mvn clean compile -DskipTests
```

#### **Verificar Dependências:**
```bash
mvn dependency:tree
```

#### **Limpar Target:**
```bash
mvn clean
```

#### **Executar em Porta Diferente:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

#### **Executar com Profile Específico:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

---

## ✅ **Verificando se a Aplicação Está Funcionando**

### **1. Verificar Status da Aplicação**
```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {"database": "H2"}
    }
  }
}
```

### **2. Acessar Interfaces Web**

#### **Swagger UI (Documentação das APIs):**
- **URL:** http://localhost:8080/swagger-ui.html
- **Descrição:** Interface para testar todas as APIs

#### **H2 Console (Banco de Dados):**
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:inventorydb`
- **User:** `sa`
- **Password:** `password`

### **3. Testar API Básica**
```bash
# Consultar produtos com estoque baixo
curl http://localhost:8080/api/v1/inventario/estoque/baixo

# Consultar estoque da loja 1
curl http://localhost:8080/api/v1/inventario/estoque/loja/1
```

---

## 🚨 **Troubleshooting - Problemas Comuns**

### **❌ Problema: "mvn: command not found"**
**Solução:**
```bash
# Windows (Chocolatey)
choco install maven

# macOS (Homebrew)  
brew install maven

# Ubuntu/Debian
sudo apt update && sudo apt install maven

# Verificar instalação
mvn -version
```

### **❌ Problema: "JAVA_HOME not set"**
**Solução:**
```bash
# Verificar onde está o Java
which java
java -version

# Linux/macOS - adicionar ao ~/.bashrc ou ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Windows - Variáveis de Ambiente
# JAVA_HOME = C:\Program Files\Java\jdk-17
# PATH = %JAVA_HOME%\bin;%PATH%
```

### **❌ Problema: Porta 8080 em uso**
**Soluções:**

#### **Opção 1: Mudar porta da aplicação**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

#### **Opção 2: Parar processo na porta 8080**
```bash
# Linux/macOS
sudo lsof -t -i:8080 | xargs sudo kill -9

# Windows
netstat -a -n -o | findstr :8080
taskkill /F /PID <PID>
```

### **❌ Problema: Dependências não baixaram**
**Solução:**
```bash
# Forçar redownload das dependências
mvn clean install -U

# Limpar cache local do Maven
rm -rf ~/.m2/repository
mvn clean install
```

### **❌ Problema: "Could not resolve dependencies"**
**Solução:**
```bash
# Verificar conectividade com repositório Maven
ping repo1.maven.org

# Configurar proxy se necessário (em ~/.m2/settings.xml)
# Ou usar repositório local/corporativo
```

### **❌ Problema: IntelliJ não reconhece o projeto**
**Soluções:**
1. **File → Invalidate Caches and Restart**
2. **Delete** pasta `.idea` e reimportar projeto
3. **Maven Tool Window → Reload All Maven Projects**

### **❌ Problema: Eclipse mostra erros de compilação**
**Soluções:**
1. **Project → Clean → Clean All Projects**
2. **Right-click project → Maven → Reload Projects**
3. **Project → Properties → Java Build Path → Reconfigure**

---

## 🎯 **Configurações Adicionais**

### **Configurar Logs Detalhados:**
```properties
# Adicionar em src/main/resources/application.properties
logging.level.com.inventory=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### **Configurar Múltiplos Perfis:**
```bash
# Perfil de desenvolvimento
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# Perfil de produção  
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

### **Executar com Debug Remoto:**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

**Depois conectar debugger na porta 5005**

---

## 📚 **Scripts de Conveniência**

### **Script para Linux/macOS (run.sh):**
```bash
#!/bin/bash
cd /app/backend

echo "🚀 Iniciando Sistema de Inventário..."
echo "📍 Verificando pré-requisitos..."

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Instale Java 17+"
    exit 1
fi

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Instale Maven 3.6+"
    exit 1
fi

echo "✅ Pré-requisitos OK"
echo "🔧 Compilando projeto..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Compilação bem-sucedida"
    echo "🚀 Iniciando aplicação..."
    mvn spring-boot:run
else
    echo "❌ Falha na compilação"
    exit 1
fi
```

### **Script para Windows (run.bat):**
```batch
@echo off
cd /d "%~dp0backend"

echo 🚀 Iniciando Sistema de Inventário...
echo 📍 Verificando pré-requisitos...

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java não encontrado. Instale Java 17+
    pause & exit /b 1
)

mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven não encontrado. Instale Maven 3.6+
    pause & exit /b 1
)

echo ✅ Pré-requisitos OK
echo 🔧 Compilando projeto...
mvn clean compile

if %errorlevel% equ 0 (
    echo ✅ Compilação bem-sucedida
    echo 🚀 Iniciando aplicação...
    mvn spring-boot:run
) else (
    echo ❌ Falha na compilação
    pause & exit /b 1
)
```

---

## 🏁 **Resumo dos Comandos Principais**

| Ação | IntelliJ | Eclipse | Terminal |
|------|----------|---------|----------|
| **Importar** | Open pom.xml | Import → Maven Projects | `cd /app/backend` |
| **Compilar** | Build Project (Ctrl+F9) | Project → Build All | `mvn clean compile` |
| **Executar** | Run (Ctrl+Shift+F10) | Run As → Java Application | `mvn spring-boot:run` |
| **Debug** | Debug (Ctrl+Shift+F9) | Debug As → Java Application | `mvn spring-boot:run -Xdebug` |
| **Testes** | Run Tests | Run As → JUnit Test | `mvn test` |

### **URLs de Acesso:**
- 🌐 **Swagger UI:** http://localhost:8080/swagger-ui.html
- 🗄️ **H2 Console:** http://localhost:8080/h2-console  
- 🏥 **Health Check:** http://localhost:8080/actuator/health

**Pronto! Seu sistema de inventário está rodando e pronto para uso!** 🎉