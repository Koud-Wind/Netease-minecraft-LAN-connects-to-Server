: 确保你是jdk17+
for %%F in (".\mohist-1.20.1-*-server.jar") do (
	java -jar %%F
    )
)

pause