package com.example.atividadeav_yann_lucas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atividadeav_yann_lucas.model.Estoque
import com.example.atividadeav_yann_lucas.model.Produto
import com.example.atividadeav_yann_lucas.ui.theme.AtividadeAv_Yann_LucasTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun TelaCadastroProduto(navController: NavHostController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastro de Produto", fontSize = 25.sp)

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") }
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") }
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") }
        )
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade") }
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            if (nome.isEmpty() || categoria.isEmpty() || preco.isEmpty() || quantidade.isEmpty()) {
                Toast.makeText(context, "Todos os campos são obrigatórios!", Toast.LENGTH_SHORT).show()
            } else if (preco.toDouble() <= 0 || quantidade.toInt() < 1) {
                Toast.makeText(context, "Preço deve ser maior que 0 e quantidade maior que 0", Toast.LENGTH_SHORT).show()
            } else {
                val novoProduto = Produto(nome, categoria, preco.toDouble(), quantidade.toInt())
                Estoque.adicionarProduto(novoProduto)
                navController.navigate("listaProdutos")
            }
        }) {
            Text(text = "Cadastrar Produto")
        }
    }
}

@Composable
fun TelaListaProdutos(navController: NavHostController) {
    val listaProdutos = Estoque.listarProdutos()

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Lista de Produtos",
            fontSize = 25.sp,
            modifier = Modifier.padding(10.dp)
        )

        LazyColumn {
            items(listaProdutos) { produto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${produto.nome} (${produto.quantidade} unidades)")
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("detalhesProduto/$produtoJson")
                    }) {
                        Text(text = "Detalhes")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("estatisticas")
        }) {
            Text(text = "Ver Estatísticas")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("cadastroProduto")
        }) {
            Text(text = "Cadastrar Novo Produto")
        }
    }
}

@Composable
fun TelaEstatisticas(navController: NavHostController) {
    val valorTotal = Estoque.calcularValorTotalEstoque()
    val quantidadeTotal = Estoque.calcularQuantidadeTotalProdutos()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estatísticas do Estoque", fontSize = 25.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Valor Total do Estoque: R$ ${"%.2f".format(valorTotal)}")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Quantidade Total de Produtos: $quantidadeTotal")
        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            navController.navigate("listaProdutos")
        }) {
            Text(text = "Voltar para Lista de Produtos")
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastroProduto") {
        composable("cadastroProduto") { TelaCadastroProduto(navController) }
        composable("listaProdutos") { TelaListaProdutos(navController) }
        composable("detalhesProduto/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            TelaDetalhesProduto(navController, produto)
        }
        composable("estatisticas") { TelaEstatisticas(navController) }
    }
}


@Composable
fun TelaDetalhesProduto(navController: NavHostController, produto: Produto) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "DETALHES DO produto")
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Nome: ${produto.nome}")
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Categoria: ${produto.categoria}")
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Preço: ${produto.preco}")
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Quantidade: ${produto.quantidade}")

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Voltar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AppNavigation()
}