import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../blocs/profile/profile_cubit.dart';
import '../blocs/profile/profile_state.dart';
import '../blocs/auth/auth_bloc.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  final _nameController = TextEditingController();

  @override
  void initState() {
    super.initState();
    context.read<ProfileCubit>().fetchProfile();
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mi Perfil'),
        actions: [
          IconButton(
            icon: const Icon(Icons.directions_car),
            onPressed: () {
              context.push('/vehicles');
            },
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              context.read<AuthBloc>().add(AuthSignOutRequested());
              context.go('/login');
            },
          )
        ],
      ),
      body: BlocConsumer<ProfileCubit, ProfileState>(
        listener: (context, state) {
          if (state is ProfileLoaded) {
            // Corrección aplicada aquí para manejar el String nullable
            _nameController.text = state.user.nombre ?? '';
          } else if (state is ProfileError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(state.message)),
            );
          }
        },
        builder: (context, state) {
          if (state is ProfileLoading || state is ProfileInitial) {
            return const Center(child: CircularProgressIndicator());
          }

          if (state is ProfileLoaded) {
            final user = state.user;
            return SingleChildScrollView(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const SizedBox(height: 20),
                  CircleAvatar(
                    radius: 50,
                    backgroundImage: user.fotoUrl != null && user.fotoUrl!.isNotEmpty
                        ? NetworkImage(user.fotoUrl!)
                        : null,
                    child: user.fotoUrl == null || user.fotoUrl!.isEmpty
                        ? const Icon(Icons.person, size: 50)
                        : null,
                  ),
                  const SizedBox(height: 20),
                  Text(
                    'Correo electrónico: ${user.email}',
                    style: Theme.of(context).textTheme.bodyLarge,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 30),
                  TextField(
                    controller: _nameController,
                    decoration: const InputDecoration(labelText: 'Nombre Completo'),
                  ),
                  const SizedBox(height: 24),
                  ElevatedButton(
                    onPressed: () {
                      final newName = _nameController.text.trim();
                      if (newName.isNotEmpty) {
                        context.read<ProfileCubit>().updateProfile(newName, photoUrl: user.fotoUrl);
                      }
                    },
                    child: const Text('Guardar Cambios'),
                  ),
                  const Divider(height: 48),
                  ListTile(
                    leading: const Icon(Icons.directions_car),
                    title: const Text('Mis Vehículos'),
                    onTap: () {
                      context.push('/vehicles');
                    },
                  ),
                  ListTile(
                    leading: const Icon(Icons.account_balance_wallet),
                    title: const Text('Perfil Bancario / Descuentos'),
                    onTap: () {
                      context.push('/bank_profile');
                    },
                  ),
                ],
              ),
            );
          }
          return const Center(child: Text('Error al cargar perfil'));
        },
      ),
    );
  }
}